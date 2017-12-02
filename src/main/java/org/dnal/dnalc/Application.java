package org.dnal.dnalc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.api.DNALCompiler;
import org.dnal.api.DataSet;
import org.dnal.api.impl.CompilerImpl;
import org.dnal.compiler.codegen.java.JavaCodeGen;
import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.compiler.generate.OutputGenerator;
import org.dnal.compiler.generate.json.JSONGenerator;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.logger.Log;
import org.dnal.dnalc.cmdline.CmdLineArgParser;
import org.dnal.dnalc.cmdline.Command;
import org.dnal.dnalc.cmdline.GenerateCommand;
import org.dnal.dnalc.cmdline.ValidateCommand;
import org.dnal.dnalc.cmdline.VersionCommand;

/**
	 * Does not use slf4j logging
	 * @author ian
	 *
	 */
	public class Application {
//		private boolean useOptions = true;
		private ConfigFileLoader configLoader;
		private boolean wasSuccessful = true;
		private boolean debug = false;
		private Map<String,OutputGenerator> generatorMap = new HashMap<>();
		private ConfigFileOptions configFileOptions;
		
		public Application(ConfigFileLoader configLoader) {
			this.configLoader = configLoader;
			Log.useSLFLogging = false;
		}
		
		public void registerGenerator(String outputType, OutputGenerator visitor) {
		    generatorMap.put(outputType, visitor);
		}
		
		private void log(String s) {
			Log.log(s);
		}
		private void logDebug(String s) {
			if (debug) {
				Log.debugLog(s);
			}
		}
		
		public void run(String[] args) {
			logDebug("dnalc run!");
			Command cmd = parseCmdLine(args);
			
			if (cmd instanceof VersionCommand) {
				log(String.format("dnalc v%s", Version.VERSION));
			} else if (cmd instanceof ValidateCommand) {
				doValidate((ValidateCommand)cmd);
			} else if (cmd instanceof GenerateCommand) {
				doGenerate((GenerateCommand)cmd);
			} else {
				String cmdname = (cmd == null) ? "?" : cmd.getClass().getSimpleName();
				log(String.format("error: unknown cmd {}!", cmdname));
				wasSuccessful = false;
			}
		}
		
		private void doValidate(ValidateCommand cmd) {
            MySimpleVisitor visitor = null;
            doIt(cmd.srcPath, visitor, cmd.customRulePackages, cmd.perfSummaryEnabled);
		}
		private void doGenerate(GenerateCommand cmd) {
			registerGenerator("text/simple", new MySimpleVisitor());
			registerGenerator("java/dnal", new JavaCodeGen());
			registerGenerator("json", new JSONGenerator());
			
		    String outputType = cmd.outputType;
		    OutputGenerator visitor = generatorMap.get(outputType);
		    if (visitor == null) {
                log(String.format("no generator for outputType '%s'", outputType));
                return;
		    }
		    
		    visitor.setOptions(configFileOptions);
			doIt(cmd.srcPath, visitor, cmd.customRulePackages, cmd.perfSummaryEnabled);
		}
        private void doIt(String srcPath, OutputGenerator visitor, List<String> customRulePackages, boolean perfSummaryEnabled) {
            DNALCompiler compiler = new CompilerImpl();
//            compiler.getCompilerOptions().useMockImportLoader(true); //!!
            addCustomRules(compiler, customRulePackages);
            
            logDebug("compile..");
            DataSet dataSet = compiler.compile(srcPath, visitor);
            boolean b = (dataSet != null);
            logDebug(String.format("compile: %b", b));
            if (! b) {
                log(String.format("%d error(s).", compiler.getErrors().size()));
                for(NewErrorMessage err: compiler.getErrors()) {
                    if (err.getLineNum() == 0) {
                        String msg = String.format(" error: %s", err.getMessage());
                        log(msg);
                    } else {
                        String msg = String.format(" line %d: %s", err.getLineNum(), err.getMessage());
                        log(msg);
                    }
                }
                return;
            } else {
                log("0 error(s).");
            }
            
            if (visitor != null ) {
                try {
                    visitor.finish();
                } catch (Exception e) {
                    log("EXCEPTION: in visitor.finsih() " + e.getMessage());
                }
            }
            
            if (perfSummaryEnabled) {
                CompilerImpl impl = (CompilerImpl) compiler;
                impl.getContext().perf.dump();
            }
        }
        

		private void addCustomRules(DNALCompiler compiler, List<String> customRulePackages) {
		    if (customRulePackages != null && ! customRulePackages.isEmpty()) {
		        RuleFactoryFinder finder = new RuleFactoryFinder();
		        List<RuleFactory> list = finder.findFactories(customRulePackages);
		        
		        for(RuleFactory factory: list) {
		            compiler.registryRuleFactory(factory);
		        }
		    }
        }

        private CmdLineArgParser createParser() {
			CmdLineArgParser parser = new CmdLineArgParser(configLoader);
			return parser;
		}
		
		private Command parseCmdLine(String[] args) {
			CmdLineArgParser parser = createParser();
			Command cmd = parser.parse(args);
			//log(String.format("%d errors", parser.getErrorCount()));
			if (parser.getErrorCount() > 0) {
				wasSuccessful = false;
			}

			if (cmd != null) {
				debug = cmd.debug;
				if (debug) {
					Log.debugLogging = true;
				}
			}
			
			this.configFileOptions = parser.getConfigFileOptions();
			return cmd;
		}
		
		public boolean wasSuccessful() {
			return wasSuccessful;
		}
	}