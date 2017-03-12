package org.dnal.api.systest;

import static org.junit.Assert.*;

import java.util.Date;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.junit.Test;

public class SysTests extends SysTestBase {

    @Test
    public void testEmpty() {
        chk("", 0, 0);
        chk("let x int = 45", 0, 1);
    }
    @Test
    public void test0() {
        chk("type Foo int end", 1, 0);
    }
    @Test
    public void testT1() {
        chkFail("type Foo int end type Foo int end", 1, "type name 'Foo' has already been defined");
//        dataSetLoaded.getInternals().getRegistry().dump();
    }
    
    @Test
    public void testT2() {
        //9 types
        DType dtype = chkType("Foo", "type Foo int end ", 1, 0);
        assertNotNull(dtype.getBaseType());
        dtype = chkType("Foo", "type Foo long end ", 1, 0);
        assertNotNull(dtype.getBaseType());
        dtype = chkType("Foo", "type Foo number end ", 1, 0);
        assertNotNull(dtype.getBaseType());
        dtype = chkType("Foo", "type Foo boolean end ", 1, 0);
        assertNotNull(dtype.getBaseType());
        dtype = chkType("Foo", "type Foo string end ", 1, 0);
        assertNotNull(dtype.getBaseType());
        dtype = chkType("Foo", "type Foo date end ", 1, 0);
        assertNotNull(dtype.getBaseType());
        
        dtype = chkType("Foo", "type Foo list<string> end ", 1, 0);
        assertNull(dtype.getBaseType());
        dtype = chkType("Foo", "type Foo enum {} end ", 1, 0);
        assertNull(dtype.getBaseType());
        dtype = chkType("Foo", "type Foo struct {} end ", 1, 0);
        assertNull(dtype.getBaseType());
    }
    
    @Test
    public void testT2A() {
        DType dtype = chkType("Foo", "type Base int end type Foo Base end", 2, 0);
        chkBaseType(dtype, "Base", "Foo");
        dtype = chkType("Foo", "type Base list<string> end type Foo Base end", 2, 0);
        chkBaseType(dtype, "Base", "Foo");
        dtype = chkType("Foo", "type Base enum {} end type Foo Base {} end", 2, 0);
        chkBaseType(dtype, "Base", null);
        dtype = chkType("Foo", "type Base struct {} end type Foo Base {} end", 2, 0);
        chkBaseType(dtype, "Base", null);
    }
    
    @Test
    public void testT3() {
        //scalar types
        DValue dval = chkValue("x", "type Foo int end let x Foo = 14", 1, 1);
        assertEquals(14, dval.asInt());
        dval = chkValue("x", "type Foo long end let x Foo = 14", 1, 1);
        assertEquals(14, dval.asLong());
        
        dval = chkValue("x", "type Foo boolean end let x Foo = true", 1, 1);
        assertEquals(true, dval.asBoolean());
        dval = chkValue("x", "type Foo number end let x Foo = -1003.2", 1, 1);
        assertEquals(-1003.2, dval.asNumber(), 0.001);
        dval = chkValue("x", "type Foo string end let x Foo = 'abc'", 1, 1);
        assertEquals("abc", dval.asString());
        
        dval = chkValue("x", "type Foo date end let x Foo = '2001-07-04T12:08:56.235-0700'", 1, 1);
        Date dt = makeDate("2001-07-04T12:08:56.235-0700");
        assertEquals(dt, dval.asDate());
    }
    
    @Test
    public void testT4() {
        DValue dval = chkValue("x", "type Foo list<string> end let x Foo = ['abc', 'def']", 1, 1);
        assertEquals(2, dval.asList().size());
        assertEquals("abc", dval.asList().get(0).asString());
        assertEquals("def", dval.asList().get(1).asString());
        
        dval = chkValue("x", "type Foo enum { RED BLUE } end let x Foo = BLUE", 1, 1);
        assertEquals("BLUE", dval.asString());

        dval = chkValue("x", "type Foo struct { x int y string } end let x Foo = { 15, 'abc' }", 1, 1);
        assertEquals(2, dval.asMap().size());
        assertEquals(15, dval.asMap().get("x").asInt());
        assertEquals("abc", dval.asMap().get("y").asString());
    }
    @Test
    public void testT4a() {
        String err = "can't assign null unless field is optional: x";
        chkFail("type Foo struct { x int y string } end let x Foo = { null, 'abc' }", 1, err);
    }
    @Test
    public void testT4b() {
        DValue dval = chkValue("x", "type Foo struct { x int optional y string } end let x Foo = { null, 'abc' }", 1, 1);
        assertEquals(2, dval.asMap().size());
        assertEquals(null, dval.asMap().get("x"));
        assertEquals("abc", dval.asMap().get("y").asString());
    }
    @Test
    public void testT4c() {
        String err = "fieldName 'y' can't be null. is not optional";
        chkFail("type Foo struct { x int y string } end let x Foo = { 15 }", 1, err);
    }

    @Test
    public void testT5a() {
        chkFail("type Foo struct { x- int  } end let x Foo = { 15 }", 1, "IDENTIFIER or list expected, - encountered");
        
    }
    @Test
    public void testT5b() {
        chkFail("type Foo struct { x int x string } end let x Foo = { 15, 'abc' }", 2, "field name already used: x");
    }
    
    @Test
    public void testT6() {
        String src1 = "type Foo struct { x int y int} end ";
        String src2 = "type Bar struct { a string foos list<Foo> } end ";
        String src3 = "type Circle struct { bar Bar } end ";
//        String src4 = "let x Circle = { [ { 'abc', [ ] } ] }";
        String src4 = "let x Circle = {  { 'abc', [ ] }  }";
        DValue dval = chkValue("x", src1 + src2 + src3 + src4, 4, 1);
        assertEquals("abc", dval.asMap().get("bar").asMap().get("a").asString());
    }
    
    @Test
    public void testT10() {
        chkFail("type Base Base end", 1, "type 'Base' - unknown Base");
    }
    
}
