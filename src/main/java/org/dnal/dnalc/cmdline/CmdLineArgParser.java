package org.dnal.dnalc.cmdline;

import java.io.File;
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
			parseOptions();
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
				command.configPath = rest;
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
					GenerateCommand gencmd = (GenerateCommand) command;
					gencmd.outputDir = rest;
				} else {
					failWith("this option only can be used with 'generate'");
				}
				break;
			case "-t":
			case "--output":
				if (command instanceof GenerateCommand) {
					GenerateCommand gencmd = (GenerateCommand) command;
					gencmd.outputType = rest;
				} else {
					failWith("this option only can be used with 'generate'");
				}
				break;
				
			default:
				putbackArg();
				done = true;
				break;
			}
			
//			if (command.configPath == null) {
//				String defaultConfigFile = "dnal-config.dnal";
//				File f = new File("./" + defaultConfigFile);
//				if (f.exists()) {
//					command.configPath = defaultConfigFile;
//				}
//			}
		}
		
		if (command.configPath == null) {
			command.configPath = setDefaultConfigPath();
		}
		
		if (command.configPath != null) {
			readConfigFile();
		} else {
			if (command instanceof GenerateCommand) {
				GenerateCommand gencmd = (GenerateCommand) command;
				if (gencmd.outputType == null) {
					gencmd.outputType = "none";
				}
				
				if (gencmd.outputDir == null) {
					gencmd.outputDir = ".";
				}
			}
		}
		
	}
	
	private String setDefaultConfigPath() {
		String path = "./dnalc.properties";
		File f = new File(path);
		if (f.exists()) {
			return path;
		}
		return null;
	}

	private void readConfigFile() {
		Log.debugLog(String.format("loading %s", command.configPath));
		ConfigFileOptions options = configLoader.load(command.configPath);
		this.configFileOptions = options;

		if (command instanceof GenerateCommand) {
			GenerateCommand gencmd = (GenerateCommand) command;
			if (options.outputPath != null) {
				gencmd.outputDir = options.outputPath;
			}
			
			if (options.outputType != null) {
				gencmd.outputType = options.outputType;
			}
			
			if (options.customRulePackages != null) {
                gencmd.customRulePackages = parseCommaSeparatedList(options.customRulePackages);
			}
		} else if (command instanceof ValidateCommand) {
		    ValidateCommand valcmd = (ValidateCommand) command;
            if (options.customRulePackages != null) {
                valcmd.customRulePackages = parseCommaSeparatedList(options.customRulePackages);
            }
        }
	}
	private List<String> parseCommaSeparatedList(String input) {
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