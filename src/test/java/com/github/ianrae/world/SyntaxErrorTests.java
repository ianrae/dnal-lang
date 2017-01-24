package com.github.ianrae.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.dnal.core.ErrorMessage;
import org.dnal.core.logger.ErrorMessageLogger;
import org.dnal.core.logger.Log;
import org.junit.Test;

import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.DataSet;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.impoter.MockImportLoader;
import com.github.ianrae.dnalparse.parser.error.ErrorTrackingBase;

public class SyntaxErrorTests extends BaseWorldTest {
    
    private static final String GENERATE_DIR = "./src/main/resources/test/bad/";
    
    @Test
    public void test() {
        loadBad("nosuchfile.dnal", "can't find file");
    }
    @Test
    public void test2() {
    }
    @Test
    public void test3() {
        loadBad("bad1.dnal", "typez encountered");
    }
    @Test
    public void test4() {
        loadBad("bad2.dnal", "= expected, == encountered");
    }
    @Test
    public void test5() {
        loadBad("drawing-bad1.dnal", "has unknown type 'XX'");
    }
    @Test
    public void test6() {
        loadBad("drawing-bad2.dnal", "struct base type 'Circle' is not a struct type");
    }
    
    
    private DataSet loadBad(String dnalFilename, String expectedError) {
        XErrorTracker.logErrors = true;
        Log.debugLogging = true;
        
        String path = GENERATE_DIR + dnalFilename;
        DNALCompiler compiler = createCompiler();
        boolean foundError = false;
        DataSet dataSet = compiler.compile(path);
        assertNull(dataSet);
        
        ErrorMessageLogger.dump(compiler.getErrors());
        for(ErrorMessage err: compiler.getErrors()) {
            if (err.getMessage().contains(expectedError)) {
                foundError = true;
            }
        }
        assertEquals(true, foundError);
        return dataSet;
    }
    
}
