package ini;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.Gson;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

import ini.analysis.spin.Ini2Pml;
import ini.ast.AstNode;
import ini.ast.Executable;
import ini.ast.Invocation;
import ini.broker.CoreBrokerClient;
import ini.broker.DeployRequest;
import ini.eval.Context;
import ini.eval.IniDebug;
import ini.eval.IniEval;
import ini.eval.data.FutureData;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;

public class Main {

	public static final Logger LOGGER = LoggerFactory.getLogger("ini");

	public static final String VERSION = "pre-alpha 2";

	public static void main(String[] args) throws Exception {

		MDC.put("node", ".");

		JSAP jsap = new JSAP();

		jsap.registerParameter(
				new Switch("help").setShortFlag('h').setLongFlag("help").setHelp("Prints this usage message."));

		jsap.registerParameter(new Switch("version").setLongFlag("version").setHelp("Print the INI version and exit."));

		jsap.registerParameter(new FlaggedOption("env").setLongFlag("env").setStringParser(JSAP.STRING_PARSER)
				.setRequired(false).setList(false).setHelp(
						"Defines the environment name to be used, as defined in the 'ini_conf.json' file. Overrides the value defined in the INI_ENV system environment variable."));

		jsap.registerParameter(new Switch("shell").setShortFlag('s').setLongFlag("shell")
				.setHelp("Starts INI in shell mode so that the user can interact with INI by typing statements."));

		jsap.registerParameter(new FlaggedOption("node").setLongFlag("node").setShortFlag('n')
				.setStringParser(JSAP.STRING_PARSER).setRequired(false).setList(false).setHelp(
						"Sets the node name and starts INI in deamon mode. The name of the node is used by other INI nodes to spawn and fetch processes and functions. This option overrides the value defined in the INI_NODE system environment variable or in the 'ini_conf.json'."));

		jsap.registerParameter(new FlaggedOption("model-out").setLongFlag("model-out")
				.setStringParser(JSAP.STRING_PARSER).setRequired(false).setList(false).setHelp(
						"Generates the Promela model into the given file, so that it can be checked by the Spin model checker."));

		jsap.registerParameter(new UnflaggedOption("file").setStringParser(JSAP.STRING_PARSER).setRequired(false)
				.setList(false)
				.setHelp("The INI file that must be parsed/executed (may contain a main function to be executed)."));

		jsap.registerParameter(new UnflaggedOption("arg").setStringParser(JSAP.STRING_PARSER).setRequired(false)
				.setList(true).setGreedy(true)
				.setHelp("The arguments to be passed to the 'main' function, if defined in the given file."));

		JSAPResult commandLineConfig = jsap.parse(args);

		if (commandLineConfig.getBoolean("help")
				|| (!commandLineConfig.userSpecified("node") && !commandLineConfig.userSpecified("file")
						&& !commandLineConfig.userSpecified("shell") && !commandLineConfig.userSpecified("version"))) {
			printUsage(System.out, jsap);
			System.exit(0);
		}
		LOGGER.info("INI version " + VERSION);
		if (commandLineConfig.getBoolean("version")) {
			System.exit(0);
		}
		if (!commandLineConfig.success()) {
			System.err.println();
			for (java.util.Iterator<?> errs = commandLineConfig.getErrorMessageIterator(); errs.hasNext();) {
				System.err.println("Error: " + errs.next());
			}

			System.err.println();
			printUsage(System.err, jsap);
			System.exit(-1);
		}

		IniParser parser = commandLineConfig.contains("file")
				? IniParser.createParserForFile(null, null, commandLineConfig.getString("file"))
				: IniParser.createParserForCode(null, null, "process main() {}");

		try {
			parser.parse();
		} catch (Exception e) {
			if (parser.hasErrors()) {
				parser.printErrors(System.err);
				e.printStackTrace();
				return;
			} else {
				e.printStackTrace();
				return;
			}
		}

		if (commandLineConfig.userSpecified("node") || commandLineConfig.userSpecified("deamon")) {
			parser.env.deamon = true;
		}

		AstAttrib attrib = null;

		try {
			attrib = new AstAttrib(parser);
			attrib.attrib(parser);
			attrib.unify();
		} catch (Exception e) {
			if (attrib != null)
				attrib.printError(System.err, e);
			System.err.println("Java stack:");
			e.printStackTrace(System.err);
			return;
		} finally {
			if (attrib != null && attrib.hasErrors()) {
				attrib.printErrors(System.err);
				return;
			}
		}

		parseConfiguration(parser);

		String systemEnv = System.getenv("INI_ENV");
		if (systemEnv != null) {
			parser.env.environment = systemEnv;
		}

		String commandLineEnv = commandLineConfig.getString("env");
		if (commandLineEnv != null) {
			parser.env.environment = commandLineEnv;
		}

		String systemNode = System.getenv("INI_NODE");
		if (systemNode != null) {
			parser.env.node = systemNode;
		}

		if (parser.env.configuration.node != null) {
			parser.env.node = parser.env.configuration.node;
		}

		String commandLineNode = commandLineConfig.getString("node");
		if (commandLineNode != null) {
			parser.env.node = commandLineNode;
		}

		String modelOut = commandLineConfig.getString("model-out");
		if (modelOut != null) {
			Ini2Pml converter = new Ini2Pml(parser, attrib);
			converter.beforeGenerate();
			for (AstNode statement : parser.topLevels) {
				converter.generate(statement);
			}
			converter.afterGenerate();
			// System.out.println(converter.getOutput());
			FileUtils.write(new File(modelOut), converter.getOutput(), "UTF-8");
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("bin/spin -search " + modelOut);
			File outFile = new File(modelOut + ".out");
			FileUtils.write(outFile, "", "UTF-8", false);
			boolean hasError = false;
			for (String line : IOUtils.readLines(pr.getInputStream(), "UTF-8")) {
				if (line.contains("assertion violated")) {
					System.err.println("model checking error: " + line);
					hasError = true;
				}
				if (line.contains("Search not completed")) {
					System.err.println("model checking error: " + line);
					hasError = true;
				}
				FileUtils.write(outFile, line + "\n", "UTF-8", true);
			}
			if (hasError) {
				System.err.println("dumped Spin output to '" + modelOut + ".out'");
				return;
			}
		}
		IniEval eval = mainEval(parser, attrib, false, null, null,
				ArrayUtils.toStringArray(commandLineConfig.getObjectArray("arg")));

		if (commandLineConfig.getBoolean("shell")) {

			Terminal terminal = TerminalBuilder.terminal();
			LineReader reader = LineReaderBuilder.builder().terminal(terminal).history(new DefaultHistory())
					.completer(new Completer() {

						@Override
						public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
							String[] toCompleteSplitted = line.line().split("[^a-zA-Z0-9_]");
							// System.out.print(Arrays.asList(toCompleteSplitted));
							String toComplete = toCompleteSplitted[toCompleteSplitted.length - 1];
							// System.out.print("*"+toComplete+"*");
							List<String> found = new ArrayList<String>();
							for (String s : eval.invocationStack.peek().getVariables().keySet()) {
								if (s.startsWith(toComplete) && !found.contains(s)) {
									found.add(s);
								}
							}
							for (String s : eval.getRootContext().getVariables().keySet()) {
								if (s.startsWith(toComplete) && !found.contains(s)) {
									found.add(s);
								}
							}
							found.sort((s1, s2) -> s1.compareTo(s2));
							if (found.size() == 1) {
								reader.getBuffer().write(found.get(0).substring(toComplete.length()));
							} else {
								terminal.writer().println();
								int max = 0;
								for (String s : found) {
									if (s.length() > max) {
										max = s.length();
									}
								}
								int cols = 0;
								for (String s : found) {
									if (cols == 4) {
										cols = 0;
										terminal.writer().println();
									}
									terminal.writer().print(s);
									for (int i = 0; i <= max - s.length(); i++) {
										terminal.writer().print(" ");
									}
									cols++;
								}
								terminal.writer().println();
								terminal.writer().print("> " + line.line());
							}
						}
					}).variable(LineReader.HISTORY_FILE, ".ini.history")
					.variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ").build();

			reader.unsetOpt(LineReader.Option.GROUP);
			reader.unsetOpt(LineReader.Option.AUTO_GROUP);
			reader.unsetOpt(LineReader.Option.AUTO_LIST);
			reader.unsetOpt(LineReader.Option.AUTO_MENU);
			reader.unsetOpt(LineReader.Option.AUTO_PARAM_SLASH);
			reader.unsetOpt(LineReader.Option.AUTO_REMOVE_SLASH);
			
			while (true) {
				String line = null;
				try {
					line = reader.readLine("> ");
					line = line.trim();
					eval.result = null;
					eval.evalCode(attrib, line + "\n");
					if (eval.result != null) {
						if (eval.result instanceof FutureData) {
							terminal.writer().println("<future>");
						} else {
							terminal.writer().println(eval.result.toPrettyString());
						}
					}
					terminal.flush();
				} catch (EndOfFileException e) {
					break;
				} catch (UserInterruptException e) {
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static Executable getMainExecutable(IniParser parser) {
		Executable main = null;
		for (AstNode topLevel : parser.topLevels) {
			if ((topLevel instanceof Executable) && "main".equals(((Executable) topLevel).name)) {
				main = (Executable) topLevel;
			}
		}
		return main;
	}

	public static void parseConfiguration(IniParser parser) {
		try {
			parser.env.configuration = new Gson()
					.fromJson(FileUtils.readFileToString(new File("ini_config.json"), "UTF8"), ConfigurationFile.class);
		} catch (Exception e) {
			throw new RuntimeException("cannot read configuration", e);
		}
	}

	public static IniEval mainEval(IniParser parser, AstAttrib attrib, String[] args) {
		return mainEval(parser, attrib, false, null, null, args);
	}

	public static IniEval mainEval(IniParser parser, AstAttrib attrib, boolean debug, List<String> breakpoints,
			List<String> watchedVariables, String[] args) {
		IniEval returnedEval;

		MDC.put("node", parser.env.node);

		parseConfiguration(parser);

		Context context = new Context((Executable) null, null);
		IniEval eval;
		if (debug) {
			eval = new IniDebug(parser, attrib, context, breakpoints, watchedVariables);
		} else {
			eval = new IniEval(parser, attrib, context);
		}
		returnedEval = eval;
		try {
			if (parser.env.deamon) {
				LOGGER.info("Starting INI deamon...");
				parser.env.coreBrokerClient = new CoreBrokerClient(parser.env);
				parser.env.coreBrokerClient.startSpawnRequestConsumer(request -> {
					LOGGER.info("processing " + request);
					Invocation invocation = new Invocation(parser, null, request.spawnedProcessName, request.parameters
							.stream().map(data -> RawData.dataToExpression(parser, data)).collect(Collectors.toList()));
					invocation.owner = request.sourceNode;
					new Thread() {
						public void run() {
							eval.eval(invocation);
						}
					}.start();
				});

				parser.env.coreBrokerClient.startFetchRequestConsumer(request -> {
					LOGGER.info("processing " + request);
					parser.env.coreBrokerClient.sendDeployRequest(request.sourceNode, new DeployRequest(parser.env.node,
							eval.getRootContext().getExecutable(request.fetchedName)));
				});

				parser.env.coreBrokerClient.startDeployRequestConsumer(request -> {
					eval.eval(request.executable);
					Main.LOGGER.info("deployed function " + request.executable.name);
				});
			}

			LOGGER.info("Environment: " + parser.env.environment);
			if (!parser.env.deamon) {
				LOGGER.info("INI started in local mode: all channels will default to local channels");
			} else {
				LOGGER.info("INI started - node " + parser.env.node);
			}

			Executable main = null;
			for (AstNode topLevel : parser.topLevels) {
				eval.eval(topLevel);
				if ((topLevel instanceof Executable) && "main".equals(((Executable) topLevel).name)) {
					main = (Executable) topLevel;
				}
			}

			if (main != null) {
				if (main.parameters != null && main.parameters.size() > 1) {
					parser.out.println(
							"Error: main function must have no parameters or one parameter (list of strings).");
					return eval;
				}
				/*
				 * context = new Context(main); if (main.parameters != null &&
				 * main.parameters.size() == 1) {
				 * context.bind(main.parameters.get(0).name,
				 * RawData.objectToData(args)); }
				 */
				eval.invoke(main, args == null || args.length == 0 ? new Object[0] : new Object[] { args });
			}
			return eval;

		} catch (Exception e) {
			eval.printError(parser.err, e);
			parser.err.println("Java stack:");
			e.printStackTrace(parser.err);
		}
		return returnedEval;
	}

	static void printUsage(PrintStream out, JSAP jsap) {
		out.println("Usage: ini " + jsap.getUsage());
		out.println();
		out.println(jsap.getHelp());
	}

}
