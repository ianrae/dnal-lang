package org.dnal.compiler.dnalc;

import static org.junit.Assert.assertEquals;

import org.dnal.core.logger.Log;
import org.dnal.dnalc.ConfigFileOptions;
import org.dnal.dnalc.Application;
import org.junit.Test;


public class DNALCTests {

	@Test
	public void testVersion() {
		String[] args = new String[] { "--version"  };
		chkRun(args);
	}
	
	@Test
	public void testValidate() {
		Log.debugLogging = false; //for now!
//		MyWorld.debug = true;
		
		String path = GENERATE_DIR+ "int1.dnal";
		chkRun(path);
	}
	
	@Test
	public void testGenerate() {
		String path = GENERATE_DIR+ "int1.dnal";
		String[] args = new String[] { path};
		chkRun(args);
	}

	@Test
	public void testGenerateBad() {
		String path = BAD_DIR + "int-bad2.dnal";
		chkRun(path);
	}
	

    @Test
    public void testGenerateBadPath() {
        String path = BAD_DIR + "zzzzint-bad2.dnal";
        chkRun(path);
    }
	
	//--
	private static final String GENERATE_DIR = "./src/main/resources/test/generate/";
	private static final String BAD_DIR = "./src/main/resources/test/bad/";
	
    private void chkRun(String arg1) {
        String[] args = new String[] { arg1 };
        chkRun(args);
    }
	private void chkRun(String[] args) {
		MockConfigLoader configLoader = createConfigLoader();
		Application dnalc = new Application(configLoader);
		dnalc.run(args);
		assertEquals(true, dnalc.wasSuccessful());
	}
	
	private MockConfigLoader createConfigLoader() {
		MockConfigLoader loader = new MockConfigLoader();
		loader.options = new ConfigFileOptions();
		loader.options.outputPath = "mytypes";
		loader.options.outputType = "java/dnal";
		loader.options.javaPackage = "com.foo";
		loader.options.writeOutputFilesEnabled = false;
		return loader;
//		if (!useOptions) {
//			loader.options = null;
//		}
//		return loader;
	}
	

}
