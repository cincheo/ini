package ini;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

import ini.ast.Function;
import ini.ast.UserType;
import ini.eval.Context;
import ini.eval.IniDebug;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;

public class Main {

	public static Configuration configuration;

	public static String environment = "development";

	public static final String VERSION = "pre-alpha 1";

	public static void main(String[] args) throws Exception {

		JSAP jsap = new JSAP();

		jsap.registerParameter(
				new Switch("version").setShortFlag('v').setLongFlag("version").setHelp("Print the INI version."));

		jsap.registerParameter(new Switch("pretty-print-ast").setShortFlag('a').setLongFlag("pretty-print-ast")
				.setHelp("Pretty print the parsed program."));

		jsap.registerParameter(new Switch("parse-only").setShortFlag('p').setLongFlag("parse-only")
				.setHelp("Only parse the program without executing it."));

		jsap.registerParameter(new Switch("debug").setShortFlag('d').setLongFlag("debug")
				.setHelp("Execute the program in debug mode - step through the program."));

		jsap.registerParameter(
				new Switch("help").setShortFlag('h').setLongFlag("help").setHelp("Print this usage message."));

		jsap.registerParameter(new FlaggedOption("breakpoint").setList(true).setListSeparator(',').setShortFlag('b')
				.setLongFlag("breakpoint").setHelp(
						"Install breakpoints on the program when run in debug mode (ignored when not in debug mode)."));

		jsap.registerParameter(
				new FlaggedOption("watch").setList(true).setListSeparator(',').setShortFlag('w').setLongFlag("watch")
						.setHelp("Defines variables to be watched during debug (ignored when not in debug mode)."));

		jsap.registerParameter(
				new FlaggedOption("env").setStringParser(JSAP.STRING_PARSER).setRequired(false).setList(false).setHelp(
						"The environment name to be used, as defined in the 'ini_conf.json' file. Overrides the value defined in the INI_ENV system environment variable."));

		jsap.registerParameter(new UnflaggedOption("file").setStringParser(JSAP.STRING_PARSER).setRequired(true)
				.setList(false)
				.setHelp("The INI file that must be parsed/executed - must contain a main function to be executed."));

		jsap.registerParameter(new UnflaggedOption("parameters").setStringParser(JSAP.STRING_PARSER).setRequired(false)
				.setList(true).setGreedy(true).setHelp("The parameters to be passed to the main function."));

		JSAPResult commandLineConfig = jsap.parse(args);

		if (commandLineConfig.getBoolean("help")) {
			printUsage(System.out, jsap);
			System.exit(0);
		}
		if (commandLineConfig.getBoolean("version")) {
			System.out.println("INI version " + VERSION);
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

		IniParser parser = IniParser.parseFile(commandLineConfig.getString("file"));

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

		String systemEnv = System.getenv("INI_ENV");
		if(systemEnv!=null) {
			environment = systemEnv;
		}
		
		String commandLineEnv = commandLineConfig.getString("env");
		if(commandLineEnv!=null) {
			environment = commandLineEnv;
		}
		
		evalMainFunction(parser, commandLineConfig);

	}

	public static void evalMainFunction(IniParser parser, JSAPResult config) {

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
				context.bind(main.parameters.get(0).name, RawData.objectToData(config.getObjectArray("parameters")));
			}
		}
		IniEval eval;
		if (config != null && config.getBoolean("debug")) {
			eval = new IniDebug(parser, context, config);
		} else {
			eval = new IniEval(parser, context, config);
		}
		try {
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
