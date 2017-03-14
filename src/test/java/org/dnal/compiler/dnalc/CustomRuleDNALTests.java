package org.dnal.compiler.dnalc;

import static org.junit.Assert.assertEquals;

import org.dnal.core.logger.Log;
import org.dnal.dnalc.ConfigFileOptions;
import org.dnal.dnalc.DNALCApp;
import org.dnal.dnalc.MySimpleVisitor;
import org.junit.Test;


public class CustomRuleDNALTests {

	@Test
	public void testValidate() {
		Log.debugLogging = false; 

		String path = SOURCE_DIR+ "myrule1.dnal";
		String[] args = new String[] { "v", "--config=something",  path };
		chkRun(args);
	}
	
    @Test
    public void testGenerate() {
        Log.debugLogging = false; 

        String path = SOURCE_DIR+ "myrule1.dnal";
        String[] args = new String[] { "g", "--config=something",  path };
        chkRun(args);
    }
	

	//--
	private static final String SOURCE_DIR = "./src/main/resources/test/customrule/";
	
    private void chkRun(String arg1) {
        String[] args = new String[] { arg1 };
        chkRun(args);
    }
	private void chkRun(String[] args) {
		MockConfigLoader configLoader = createConfigLoader();
		DNALCApp dnalc = new DNALCApp(configLoader);
        dnalc.registerGenerator("text/simple", new MySimpleVisitor());
		dnalc.run(args);
		assertEquals(true, dnalc.wasSuccessful());
	}
	
	private MockConfigLoader createConfigLoader() {
		MockConfigLoader loader = new MockConfigLoader();
		loader.options = new ConfigFileOptions();
		loader.options.outputPath = "mytypes";
		loader.options.outputType = "text/simple";
		loader.options.customRulePackages = "com.github.ianrae.dnalparse.compiler";
		return loader;
//		if (!useOptions) {
//			loader.options = null;
//		}
//		return loader;
	}
	

}
