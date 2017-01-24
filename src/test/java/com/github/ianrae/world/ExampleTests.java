package com.github.ianrae.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.logger.Log;
import org.junit.Test;

import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.DataSet;
import com.github.ianrae.dnalparse.Generator;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.generate.json.JSONGenerator;
import com.github.ianrae.dnalparse.impl.CompilerImpl;
import com.github.ianrae.dnalparse.impoter.MockImportLoader;
import com.github.ianrae.dnalparse.parser.error.ErrorTrackingBase;

public class ExampleTests extends BaseWorldTest {
    
    private static final String GENERATE_DIR = "./src/main/resources/test/example/";
    
    @Test
    public void test() {
        DataSet dataSet = load("example.dnal");
        
        DValue val = dataSet.getValue("addresses");
        assertNotNull(val);
        List<DValue> list = val.asList();
        assertEquals(2, list.size());
        chkString(val, 0, "street", "150 Main st.");
        chkString(val, 1, "street", "160 Main st.");
        
        chkString(val, 0, "city", "ABC");
        chkString(val, 1, "city", "DEF");
        
        chkBoolean(val, 0, "flag", false);
        chkBoolean(val, 1, "flag", true);
        
        chkInt(val, 0, "size", -15);
        chkInt(val, 1, "size", 23);
        
        chkNumber(val, 0, "width", 102.4);
        chkNumber(val, 1, "width", -102.4);
    }
    @Test
    public void test2() {
        DataSet dataSet = load("example2.dnal");
        
        DValue val = dataSet.getValue("addresses");
        assertNotNull(val);
        List<DValue> list = val.asList();
        assertEquals(2, list.size());
        chkString(val, 0, "street", "150 Main st.");
        chkString(val, 1, "street", "160 Main st.");
        
        chkString(val, 0, "city", "ABC");
        chkString(val, 1, "city", "DEF");
        
        chkBoolean(val, 0, "flag", false);
        chkBoolean(val, 1, "flag", true);
        
        chkInt(val, 0, "size", -15);
        chkInt(val, 1, "size", 23);
        
        List<DValue> dlist = getList(val, 0, "x");
        assertEquals(1, dlist.size());
        assertEquals("abc", dlist.get(0).asString());
        
        dlist = getList(val, 1, "x");
        assertEquals(0, dlist.size());
        
        //x2
        dlist = getList(val, 0, "x2");
        assertEquals(2, dlist.size());
        List<DValue> x2list = dlist.get(0).asList();
        assertEquals(2, x2list.size());
        assertEquals("i1", x2list.get(0).asString());
        assertEquals("i2", x2list.get(1).asString());
        x2list = dlist.get(1).asList();
        assertEquals(1, x2list.size());
        assertEquals("i3", x2list.get(0).asString());
        
        dlist = getList(val, 1, "x");
        assertEquals(0, dlist.size());
    }
    
    @Test
    public void test3() {
        DataSet dataSet = load("example3.dnal");
        
        DValue val = dataSet.getValue("addresses");
        assertNotNull(val);
        List<DValue> list = val.asList();
        assertEquals(2, list.size());
        
        chkBoolean(val, 0, "flag", false);
        chkBoolean(val, 1, "flag", true);
        
        chkInt(val, 0, "size", -15);
        chkInt(val, 1, "size", 23);
        
        DStructHelper helper = getStruct(val, 0, "origin");
        assertEquals(10, helper.getField("x").asInt());
        assertEquals(20, helper.getField("y").asInt());
    }
    
    
//    @Test
//    public void test4() {
//        int val = 100;
//        for(int i = 0; i < 100; i++) {
//            String s = String.format(" { '%d.', \"ABC\", false, %d, [ 'abc' ] },", i, val + i);
//            log(s);
//        }
//    }    
    @Test
    public void test4() {
        DataSet dataSet = load("exampleBig2.dnal");
        
        DValue val = dataSet.getValue("addresses");
        assertNotNull(val);
        List<DValue> list = val.asList();
        assertEquals(2, list.size());
        chkString(val, 0, "street", "0.");
        chkString(val, 1, "street", "1.");
        
        chkString(val, 0, "city", "ABC");
        chkString(val, 1, "city", "ABC");
        
        chkBoolean(val, 0, "flag", false);
        chkBoolean(val, 1, "flag", false);
        
        chkInt(val, 0, "size", 100);
        chkInt(val, 1, "size", 101);
        
        log("json..");
        Generator gen = dataSet.createGenerator();
        JSONGenerator visitor = new JSONGenerator();
        boolean b = gen.generate(visitor);
        for(String s: visitor.outputL) {
            log(s);
        }
    }
    
    @Test
    public void test4a() {
//        DataSet dataSet = load("exampleBig2.dnal");
        DataSet dataSetxx = load("exampleBig.dnal");
        DataSet dataSet = load("exampleBig.dnal");
        
        log("json..");
        Generator gen = dataSet.createGenerator();
        JSONGenerator visitor = new JSONGenerator();
        boolean b = gen.generate(visitor);
//        for(String s: visitor.outputL) {
//            log(s);
//        }
        
        log("-----");
        CompilerImpl compiler = (CompilerImpl) this.aCompiler;
        compiler.getContext().perf.dump();
    }
    
    private DataSet load(String dnalFilename) {
        XErrorTracker.logErrors = true;
        Log.debugLogging = true;
        
        String path = GENERATE_DIR + dnalFilename;
        DNALCompiler compiler = createCompiler();
        DataSet dataSet = compiler.compile(path);
        assertNotNull(dataSet);
        return dataSet;
    }
    
    private List<DValue> getList(DValue val, int index, String fieldName) {
        DValue dd = getField(val, index, fieldName);
        return dd.asList();
    }
    private DStructHelper getStruct(DValue val, int index, String fieldName) {
        DValue dd = getField(val, index, fieldName);
        return dd.asStruct();
    }
    private void chkString(DValue val, int index, String fieldName, String expected) {
        DValue dd = getField(val, index, fieldName);
        assertEquals(expected, dd.asString());
    }
    private void chkBoolean(DValue val, int index, String fieldName, boolean expected) {
        DValue dd = getField(val, index, fieldName);
        assertEquals(expected, dd.asBoolean());
    }
    private void chkInt(DValue val, int index, String fieldName, int expected) {
        DValue dd = getField(val, index, fieldName);
        assertEquals(expected, dd.asInt());
    }
    private void chkNumber(DValue val, int index, String fieldName, double expected) {
        DValue dd = getField(val, index, fieldName);
        assertEquals(expected, dd.asNumber(), 0.0001);
    }
    private DValue getField(DValue val, int index, String fieldName) {
        assertNotNull(val);
        List<DValue> list = val.asList();
        DValue el = list.get(index);
        DStructHelper helper = new DStructHelper(el);
        DValue dd = helper.getField(fieldName);
        return dd;
    }
    
}
