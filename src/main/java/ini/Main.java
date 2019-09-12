package ini;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
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

import ini.ast.AstNode;
import ini.ast.Executable;
import ini.ast.Invocation;
import ini.broker.CoreBrokerClient;
import ini.broker.DeployRequest;
import ini.eval.Context;
import ini.eval.IniDebug;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;

public class Main {

	public static final Logger LOGGER = LoggerFactory.getLogger("ini");

	public static final String VERSION = "pre-alpha 2";

	public static void main(String[] args) throws Exception {

		MDC.put("node", ".");

		JSAP jsap = new JSAP();

		jsap.registerParameter(new Switch("version").setLongFlag("version").setHelp("Print the INI version and exit."));

		jsap.registerParameter(new Switch("debug").setLongFlag("debug")
				.setHelp("Execute the program in debug mode - step through the program."));

		jsap.registerParameter(new Switch("shell").setShortFlag('s').setLongFlag("shell")
				.setHelp("Start INI in shell mode so that the user can interact with INI by typing statements."));

		jsap.registerParameter(
				new Switch("help").setShortFlag('h').setLongFlag("help").setHelp("Print this usage message."));

		jsap.registerParameter(new FlaggedOption("breakpoints").setList(true).setListSeparator(',').setShortFlag('b')
				.setLongFlag("breakpoints").setHelp(
						"Install breakpoints on the program when run in debug mode (ignored when not in debug mode)."));

		jsap.registerParameter(
				new FlaggedOption("watch").setList(true).setListSeparator(',').setShortFlag('w').setLongFlag("watch")
						.setHelp("Define variables to be watched during debug (ignored when not in debug mode)."));

		jsap.registerParameter(new FlaggedOption("env").setLongFlag("env").setStringParser(JSAP.STRING_PARSER)
				.setRequired(false).setList(false).setHelp(
						"Define the environment name to be used, as defined in the 'ini_conf.json' file. Overrides the value defined in the INI_ENV system environment variable."));

		jsap.registerParameter(new Switch("deamon").setLongFlag("deamon").setShortFlag('d').setHelp(
				"Tell INI to start as a deamon and listen to spawn and deployment request. Note that a node id should be defined in the environment or in the configuration, otherwise the default node id is 'main'. In deamon mode, the INI process never returns and a file to be evaluated is optional. Note that setting the --node option also triggers the deamon mode."));

		jsap.registerParameter(new FlaggedOption("node").setLongFlag("node").setShortFlag('n')
				.setStringParser(JSAP.STRING_PARSER).setRequired(false).setList(false).setHelp(
						"Set the node id of the started INI deamon as used when a program spawns a new process (setting this option automatically sets the deamon mode). This option overrides the value defined in the INI_NODE system environment variable or in the 'ini_conf.json'. When a node id is not defined, the INI deamon starts with the 'main' node id."));

		jsap.registerParameter(new UnflaggedOption("file").setStringParser(JSAP.STRING_PARSER).setRequired(false)
				.setList(false).setHelp(
						"The INI file that must be parsed/executed - must contain a main function to be executed. If no file is passed, an endless INI process is started."));

		jsap.registerParameter(new UnflaggedOption("parameters").setStringParser(JSAP.STRING_PARSER).setRequired(false)
				.setList(true).setGreedy(true).setHelp("The parameters to be passed to the main function."));

		JSAPResult commandLineConfig = jsap.parse(args);

		if (commandLineConfig.getBoolean("help")) {
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

		IniEval eval = mainEval(parser, commandLineConfig.getBoolean("debug"),
				Arrays.asList(ArrayUtils.toStringArray(commandLineConfig.getObjectArray("breakpoints"))),
				Arrays.asList(ArrayUtils.toStringArray(commandLineConfig.getObjectArray("watch"))),
				ArrayUtils.toStringArray(commandLineConfig.getObjectArray("parameters")));

		if (commandLineConfig.getBoolean("shell")) {

			Terminal terminal = TerminalBuilder.terminal();
			LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
                    .build();
			
            while (true) {
                String line = null;
                try {
                    line = reader.readLine("> ");
                    line = line.trim();
                    eval.result = null;
					eval.evalCode(line);
					if(eval.result != null) {
						terminal.writer().println(eval.result.toPrettyString());
					}
                    terminal.flush();
                } catch(EndOfFileException e) {
                	break;
                } catch(UserInterruptException e) {
                	break;
                } catch(Exception e) {
                	e.printStackTrace();
                }
            }
                    
		}

	}

	/*
	 * private static final String TMP_FUNCTION_NAME = "_root_tmp_";
	 * 
	 * private synchronized static Context getRootContext() { }
	 * 
	 * private static void evalCommand(IniParser parser, String command) {
	 * parser.parseAdditionalCode(code); IniEval eval = new IniEval(parser, new
	 * Context(new Function(parser, null, "<command>", null, null)));
	 * eval.eval(); }
	 */

	public static void parseConfiguration(IniParser parser) {
		try {
			parser.env.configuration = new Gson()
					.fromJson(FileUtils.readFileToString(new File("ini_config.json"), "UTF8"), ConfigurationFile.class);
		} catch (Exception e) {
			throw new RuntimeException("cannot read configuration", e);
		}
	}

	public static IniEval mainEval(IniParser parser, String[] args) {
		return mainEval(parser, false, null, null, args);
	}

	public static IniEval mainEval(IniParser parser, boolean debug, List<String> breakpoints,
			List<String> watchedVariables, String[] args) {
		IniEval returnedEval;
		if (args == null) {
			args = new String[0];
		}

		MDC.put("node", parser.env.node);

		parseConfiguration(parser);

		Context context = new Context((Executable) null, null);
		IniEval eval;
		if (debug) {
			eval = new IniDebug(parser, context, breakpoints, watchedVariables);
		} else {
			eval = new IniEval(parser, context);
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
			LOGGER.info("INI started - node " + parser.env.node);

			Executable main = null;
			for (AstNode topLevel : parser.topLevels) {
				eval.eval(topLevel);
				if ((topLevel instanceof Executable) && "main".equals(((Executable) topLevel).name)) {
					main = (Executable) topLevel;
				}
			}

			if (main != null) {
				// Ini2Pml converter = new Ini2Pml(parser);
				// StringBuffer out = new StringBuffer();
				// converter.generateObserverdVariabsles(main);
				// converter.generate(main, out);
				// System.out.println(converter.variableDeclaration.toString() +
				// out.toString() + converter.initPromelaCode);
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
				eval.invoke(main, args);
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
