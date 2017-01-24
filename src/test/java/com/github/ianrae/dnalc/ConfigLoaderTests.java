package com.github.ianrae.dnalc;

import static org.junit.Assert.*;

import org.dnal.api.impl.CompilerImpl;
import org.dnal.compiler.impoter.MockImportLoader;
import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.logger.Log;
import org.junit.Test;

import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.DataSet;

public class ConfigLoaderTests {
    
    public static class DnalConfigLoader implements ConfigFileLoader {

        @Override
        public ConfigFileOptions load(String path) {
            //avoiding holding ref to parse ast objects
            DNALCompiler compiler = new CompilerImpl();
//            compiler.getCompilerOptions().useMockImportLoader(true); //!!
            DataSet dataSet = doIt(path, compiler);
            if (dataSet ==  null) {
                return null;
            }
            
            DValue dval = dataSet.getValue("options");
            if (dval == null) {
                return null;
            }
            return buildOptions(dval);
        }
        
        private ConfigFileOptions buildOptions(DValue dval) {
            ConfigFileOptions options = new ConfigFileOptions();
            DStructHelper helper = new DStructHelper(dval);
            DValue v = helper.getField("outputType");
            options.outputType = v.asString();
            
            v = helper.getField("outputPath");
            options.outputPath = v.asString();
            
            return options;
        }

        private DataSet doIt(String srcPath, DNALCompiler compiler) {
            Log.debugLog("compile..");
            DataSet dataSet = compiler.compile(srcPath, null);
            boolean b = (dataSet != null);
            Log.debugLog(String.format("compile: %b", b));
            if (! b) {
                Log.log(String.format("%d error(s).", compiler.getErrors().size()));
                for(ErrorMessage err: compiler.getErrors()) {
                    if (err.getLineNum() == 0) {
                        String msg = String.format(" error: %s", err.getMessage());
                        Log.log(msg);
                    } else {
                        String msg = String.format(" line %d: %s", err.getLineNum(), err.getMessage());
                        Log.log(msg);
                    }
                }
                return null;
            } else {
                Log.log("0 error(s).");
                return dataSet;
            }
        }
    }
	
	@Test
	public void testNone() {
	    String path = CONFIG_DIR + "config.dnal";
	    DnalConfigLoader loader = new DnalConfigLoader();
	    ConfigFileOptions options = loader.load(path);
	    assertNotNull(options);
	    assertEquals("/tmp/abc", options.outputPath);
	    assertEquals("java/dnal", options.outputType);
	}

    //--
    private static final String CONFIG_DIR = "./src/main/resources/test/config/";
	
}
