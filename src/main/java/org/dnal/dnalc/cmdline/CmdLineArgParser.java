package org.dnal.dnalc.cmdline;

import java.util.Arrays;
import java.util.List;

import org.dnal.core.logger.Log;
import org.dnal.dnalc.ConfigFileLoader;
import org.dnal.dnalc.ConfigFileOptions;

public class CmdLineArgParser {
	public final static String defaultConfigFile = "dnal-config.dnal";

	private Command command;
	private int currentArgIndex;
	private String[] args;
	private String rest;
	private int errorCount;
	private ConfigFileLoader configLoader;
	private ConfigFileOptions configFileOptions;


	public CmdLineArgParser(ConfigFileLoader loader) {
		this.configLoader = loader;
	}

	public Command parse(String[] args) {
		this.args = args;
		currentArgIndex = 0;
		command = null;
		parseAction();
		if (command instanceof VersionCommand) {
		} else {
			loadConfigFile();
			parseOptions();
			propogateOptionsToCmd();
			parseSourceFilePath();
		}


		return (errorCount == 0) ? command : null;
	}

	private void parseAction() {
		String arg = getNextArg();
		if (arg == null) {
			command = new ValidateCommand();
			return;
		}

		switch(arg) {
		case "--version":
			command = new VersionCommand();
			break;
		case "v":
		case "validate":
			command = new ValidateCommand();
			break;
		case "g":
		case "generate":
			command = new GenerateCommand();
			break;
		default:
			command = new GenerateCommand();
			putbackArg();
			break;
		}

	}


	private void parseSourceFilePath() {
		String arg = getNextArg();
		if (arg == null) {
			failWith("missing source file");
		}
		command.srcPath = arg;
	}

	private void failWith(String msg) {
		System.out.println("error: " + msg);
		errorCount++;
	}

	private void loadConfigFile() {
		String configPath = null;
		String config = findConfigArg();
		if (config != null) {
			configPath = rest;
		} else {
			configPath = "./dnalc.properties";
		}

		if (configLoader.existsConfigFile(configPath)) {
			Log.debugLog(String.format("loading %s", configPath));
			ConfigFileOptions options = configLoader.load(configPath);
			this.configFileOptions = options;
		}
	}

	private void parseOptions() {
		boolean done = false; 
		while (!done) {
			String arg = getNextArg();
			if (arg == null) {
				return;
			}

			switch(arg) {
			case "-c":
			case "--config":
				//				command.configPath = rest;
				//do nothing: handled by findConfigArg
				break;
			case "--perf":
				command.perfSummaryEnabled = true;
				break;
			case "-d":
			case "--debug":
				command.debug = true;
				Log.debugLogging = true;
				break;
			case "-o":
			case "--output-path":
				if (command instanceof GenerateCommand) {
					configFileOptions.outputPath = rest;
				} else {
					failWith("this option only can be used with 'generate'");
				}
				break;
			case "-t":
			case "--output":
				if (command instanceof GenerateCommand) {
					configFileOptions.outputType = rest;
				} else {
					failWith("this option only can be used with 'generate'");
				}
				break;

			default:
				putbackArg();
				done = true;
				break;
			}
		}
	}

	private void propogateOptionsToCmd() {
		if (configFileOptions == null) {
			return;
		}
		
		if (command instanceof GenerateCommand) {
			GenerateCommand gencmd = (GenerateCommand) command;
			gencmd.outputDir = configFileOptions.outputPath;
			gencmd.outputType = configFileOptions.outputType;
			gencmd.customRulePackages = parseCommaSeparatedList(configFileOptions.customRulePackages);
		} else if (command instanceof ValidateCommand) {
			ValidateCommand valcmd = (ValidateCommand) command;
			valcmd.customRulePackages = parseCommaSeparatedList(configFileOptions.customRulePackages);
		}
	}
	private List<String> parseCommaSeparatedList(String input) {
		if (input == null) {
			return null;
		}
		
		String[] ar = input.split(",");
		return Arrays.asList(ar);
	}

	private String getNextArg() {
		rest = null;
		if (args == null ||currentArgIndex >= args.length) {
			return null;
		}
		String arg = args[currentArgIndex++];
		if (arg != null) {
			int pos = arg.indexOf('=');
			if (pos > 0) {
				rest = arg.substring(pos + 1);
				arg = arg.substring(0, pos);
			}
		}
		return arg;
	}

	private String findConfigArg() {
		int save = currentArgIndex;

		String result = null;
		while(true) {
			String arg = getNextArg();
			if (arg == null) {
				break;
			} else if (arg.equals("-c") || arg.equals("--config")) {
				result = arg;
				break;
			}
		}

		currentArgIndex = save;
		return result;
	}

	private void putbackArg() {
		currentArgIndex--;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public ConfigFileOptions getConfigFileOptions() {
		return configFileOptions;
	}
}