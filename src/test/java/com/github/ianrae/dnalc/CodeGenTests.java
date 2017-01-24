package com.github.ianrae.dnalc;

import static org.junit.Assert.assertEquals;

import org.dval.logger.Log;
import org.junit.Test;

import com.github.ianrae.dnalparse.codegen.java.JavaCodeGen;
import com.github.ianrae.dnalparse.codegen.java.JavaOutputRenderer;


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
