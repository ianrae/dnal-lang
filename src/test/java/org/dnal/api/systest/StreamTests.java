package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.dnal.api.DNALCompiler;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.DValue;
import org.dnal.core.logger.Log;
import org.dnal.core.util.TextFileReader;
import org.junit.Test;

public class StreamTests extends SysTestBase {

    @Test
    public void testEmpty() {
        XErrorTracker.logErrors = true;
        Log.debugLogging = true;

        String source = "let x int = 45";
        InputStream stream = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
        
        DNALCompiler compiler = createCompiler();
       	dataSetLoaded = compiler.compile(stream);
       	DValue dval = dataSetLoaded.getValue("x");
       	assertEquals(45, dval.asInt());
    }
    
    @Test
    public void test2() throws Exception {
    	String path = GENERATE_DIR + "struct2.dnal";
    	File file = new File(path);
        InputStream stream = new FileInputStream(file);    	
        DNALCompiler compiler = createCompiler();
       	dataSetLoaded = compiler.compile(stream);
       	DValue dval = dataSetLoaded.getValue("other");
       	assertEquals(55, dval.asInt());
    }

    private static final String GENERATE_DIR = "./src/main/resources/test/generate/";
    
}
