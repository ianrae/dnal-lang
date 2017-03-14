package org.dnal.compiler.dnalc;

import static org.junit.Assert.assertEquals;

import org.dnal.compiler.codegen.java.JavaCodeGen;
import org.dnal.compiler.codegen.java.JavaOutputRenderer;
import org.dnal.core.logger.Log;
import org.dnal.dnalc.ConfigFileOptions;
import org.dnal.dnalc.DNALCApp;
import org.dnal.dnalc.MySimpleVisitor;
import org.junit.Test;


public class CodeGenTests {
    
    @Test
    public void testRenderer() {
        MockConfigLoader configLoader = createConfigLoader();
        JavaOutputRenderer r = new JavaOutputRenderer(configLoader.options);
        String path = r.buildOutputPath("Zoo");
        assertEquals("/tmp/mytypes/com/foo/Zoo.java", path);
    }

	@Test
	public void testGenerateScalar() {
        Log.debugLogging = false; //for now!
		String path = GENERATE_DIR + "int1.dnal";
		String[] args = new String[] { "--config=something",  path};
		chkRun(args);
	}
    @Test
    public void testGenerateScalarNumber() {
        Log.debugLogging = false; //for now!
        String path = GENERATE_DIR + "number1.dnal";
        String[] args = new String[] { "--config=something",  path};
        chkRun(args);
    }
    @Test
    public void testGenerateScalarDate() {
        Log.debugLogging = false; //for now!
        String path = GENERATE_DIR + "date1.dnal";
        String[] args = new String[] { "--config=something",  path};
        chkRun(args);
    }
    @Test
    public void testGenerateStruct() {
        Log.debugLogging = false; //for now!
        String path = GENERATE_DIR + "struct1.dnal";
        String[] args = new String[] { "--config=something",  path};
        chkRun(args);
    }


	//--
	private static final String GENERATE_DIR = "./src/main/resources/test/generate/";
	
    private void chkRun(String arg1) {
        String[] args = new String[] { arg1 };
        chkRun(args);
    }
	private void chkRun(String[] args) {
		MockConfigLoader configLoader = createConfigLoader();
		DNALCApp dnalc = new DNALCApp(configLoader);
		dnalc.registerGenerator("text/simple", new MySimpleVisitor());
		dnalc.registerGenerator("java/dnal", new JavaCodeGen(configLoader.options));
		dnalc.run(args);
		assertEquals(true, dnalc.wasSuccessful());
	}
	
	private MockConfigLoader createConfigLoader() {
		MockConfigLoader loader = new MockConfigLoader();
		loader.options = new ConfigFileOptions();
		loader.options.outputPath = "/tmp/mytypes";
//		loader.options.outputType = "text/simple";
		loader.options.outputType = "java/dnal";
		loader.options.javaPackage = "com.foo";
		return loader;
//		if (!useOptions) {
//			loader.options = null;
//		}
//		return loader;
	}
	

}
