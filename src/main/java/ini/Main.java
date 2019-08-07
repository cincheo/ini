package ini;

import java.io.File;
import java.io.PrintStream;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

import ini.ast.Function;
import ini.ast.Invocation;
import ini.ast.UserType;
import ini.broker.CoreBrokerClient;
import ini.broker.DeployRequest;
import ini.eval.Context;
import ini.eval.IniDebug;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;

public class Main {

	public static Configuration configuration;

	public static String environment = "development";

	public static String node = "main";

	public static final String VERSION = "pre-alpha 2";
	
	public static boolean verbose = false;
	
	public static void log(String msg) {
		if(verbose) {
			System.out.println("[CORE] "+msg);
		}
	}

	public static void main(String[] args) throws Exception {

		JSAP jsap = new JSAP();

		jsap.registerParameter(new Switch("version").setLongFlag("version").setHelp("Print the INI version and exit."));

		jsap.registerParameter(new Switch("verbose").setShortFlag('v').setLongFlag("verbose")
				.setHelp("Print some information about INI and its environment before running the program."));

		jsap.registerParameter(new Switch("pretty-print-ast").setShortFlag('a').setLongFlag("pretty-print-ast")
				.setHelp("Pretty print the parsed program."));

		jsap.registerParameter(new Switch("parse-only").setShortFlag('p').setLongFlag("parse-only")
				.setHelp("Only parse the program without executing it."));

		jsap.registerParameter(new Switch("debug").setLongFlag("debug")
				.setHelp("Execute the program in debug mode - step through the program."));

		jsap.registerParameter(
				new Switch("help").setShortFlag('h').setLongFlag("help").setHelp("Print this usage message."));

		jsap.registerParameter(new FlaggedOption("breakpoint").setList(true).setListSeparator(',').setShortFlag('b')
				.setLongFlag("breakpoint").setHelp(
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

		verbose = commandLineConfig.getBoolean("verbose");
		if (commandLineConfig.getBoolean("help")) {
			printUsage(System.out, jsap);
			System.exit(0);
		}
		if (commandLineConfig.getBoolean("version") || commandLineConfig.getBoolean("verbose")) {
			System.out.println("INI version " + VERSION);
		}
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

		IniParser parser = commandLineConfig.contains("file") ? IniParser.parseFile(commandLineConfig.getString("file"))
				: IniParser.parseCode("process main() {}");

		if (parser.hasErrors()) {
			parser.printErrors(System.err);
			return;
		}

		if (commandLineConfig.getBoolean("pretty-print-ast")) {
			for (UserType t : parser.parsedTypes) {
				t.prettyPrint(System.out);
			}
			for (Function f : parser.parsedFunctionList) {
				f.prettyPrint(System.out);
			}
		}

		AstAttrib attrib = null;

		try {
			attrib = attrib(parser);
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

		if (commandLineConfig.getBoolean("parse-only")) {
			return;
		}

		try {
			configuration = new Gson().fromJson(FileUtils.readFileToString(new File("ini_config.json"), "UTF8"),
					Configuration.class);
		} catch (Exception e) {
			throw new RuntimeException("cannot read configuration", e);
		}

		String systemEnv = System.getenv("INI_ENV");
		if (systemEnv != null) {
			environment = systemEnv;
		}

		String commandLineEnv = commandLineConfig.getString("env");
		if (commandLineEnv != null) {
			environment = commandLineEnv;
		}

		String systemNode = System.getenv("INI_NODE");
		if (systemNode != null) {
			node = systemNode;
		}

		if (configuration.node != null) {
			node = configuration.node;
		}

		String commandLineNode = commandLineConfig.getString("node");
		if (commandLineNode != null) {
			node = commandLineNode;
		}

		evalMainFunction(parser, commandLineConfig);

	}

	public static void evalMainFunction(IniParser parser, JSAPResult commandLineConfig) {

		try {
			configuration = new Gson().fromJson(FileUtils.readFileToString(new File("ini_config.json"), "UTF8"),
					Configuration.class);

			// System.err.println("=====> " + new Gson().toJson(configuration));
		} catch (Exception e) {
			throw new RuntimeException("cannot read configuration", e);
		}

		Context context = null;
		Function main = parser.parsedFunctionMap.get("main");
		if (main == null) {
			parser.out.println("Error: no main function found.");
			return;
		} else {
			// Ini2Pml converter = new Ini2Pml(parser);
			// StringBuffer out = new StringBuffer();
			// converter.generateObserverdVariabsles(main);
			// converter.generate(main, out);
			// System.out.println(converter.variableDeclaration.toString() +
			// out.toString() + converter.initPromelaCode);
			if (main.parameters != null && main.parameters.size() > 1) {
				parser.out.println("Error: main function must have no parameters or one parameter (list of strings).");
				return;
			}
			context = new Context(main);
			// System.out.println("=====>
			// "+Arrays.asList(config.getObjectArray("parameters")));
			if (main.parameters != null && main.parameters.size() == 1) {
				context.bind(main.parameters.get(0).name,
						RawData.objectToData(commandLineConfig.getObjectArray("parameters")));
			}
		}
		IniEval eval;
		if (commandLineConfig != null && commandLineConfig.getBoolean("debug")) {
			eval = new IniDebug(parser, context, commandLineConfig);
		} else {
			eval = new IniEval(parser, context, commandLineConfig);
		}
		try {

			if (commandLineConfig != null
					&& (commandLineConfig.getBoolean("deamon") || commandLineConfig.contains("node"))) {
				if (commandLineConfig != null && commandLineConfig.getBoolean("verbose")) {
					System.out.println("Starting INI deamon...");
				}
				CoreBrokerClient.startSpawnRequestConsumer(request -> {
					Invocation invocation = new Invocation(parser, null, request.spawnedProcessName, request.parameters
							.stream().map(data -> RawData.dataToExpression(parser, data)).collect(Collectors.toList()));
					invocation.owner = request.sourceNode;
					eval.eval(invocation);
				});

				CoreBrokerClient.startFetchRequestConsumer(request -> {
					if (parser.parsedFunctionMap.containsKey(request.fetchedName)) {
						CoreBrokerClient.sendDeployRequest(request.sourceNode,
								new DeployRequest(parser.parsedFunctionMap.get(request.fetchedName)));
					}
				});
			}

			if (commandLineConfig != null && commandLineConfig.getBoolean("verbose")) {
				System.out.println("Environment: " + environment);
				System.out.println("INI started - node " + node);
			}

			eval.eval(main);
		} catch (Exception e) {
			eval.printError(parser.err, e);
			parser.err.println("Java stack:");
			e.printStackTrace(parser.err);
		}
	}

	static void printUsage(PrintStream out, JSAP jsap) {
		out.println("Usage: ini " + jsap.getUsage());
		out.println();
		out.println(jsap.getHelp());
	}

	public static AstAttrib attrib(IniParser parser) throws Exception {
		AstAttrib attrib = new AstAttrib(parser);
		attrib.createTypes();

		if (!attrib.hasErrors()) {
			for (Function f : parser.parsedFunctionList) {
				attrib.invoke(f);
			}
			// attrib.printConstraints(System.out);
			attrib.unify();
			// System.out.println("===============================");
			// attrib.printConstraints(System.out);
			// System.err.println("===============================");
			// System.err.println(Type.types);
			// System.err.println(Type.aliases);
			// System.err.println(Constructor.constructors);
		}
		return attrib;
	}

}
