package com.github.ianrae.dnalparse.systest;

import static org.junit.Assert.assertEquals;

import org.dval.DValue;
import org.junit.Test;

public class ImportSysTests extends SysTestBase {

    
    @Test
    public void testT300() {
        chkFail("type Foo struct { x int y int} end package xyz ", 1, "package must be first statement");
    }
    @Test
    public void testT301() {
        chkFail("package abc package def type Foo struct { x int y int} end", 1, "not allowed to have more than one package statement");
    }
    
    @Test
    public void testT302() {
        String src1 = "package xyz type Foo int end let x Foo = 45";
        DValue dval = chkValue("xyz.x", src1, 1, 1);
        assertEquals(45, dval.asInt());
    }
    
    @Test
    public void testT303() {
        String src1 = "package xyz type Foo struct { x int y int} end ";
        String src2 = "type Bar struct { a string foos list<Foo> } end ";
        String src3 = "type Circle struct { bar Bar } end ";
//        String src4 = "let x Circle = { [ { 'abc', [ ] } ] }";
        String src4 = "let x Circle = {  { 'abc', [ ] }  }";
        DValue dval = chkValue("xyz.x", src1 + src2 + src3 + src4, 4, 1);
        assertEquals("abc", dval.asMap().get("bar").asMap().get("a").asString());
        this.registry.dump();
        this.world.dump();
    }
    
    @Test
    public void testT320() {
        chkFail("type Foo struct { x int y int} end import xyz ", 1, "import must be before type or value statements");
        chkFail("package abc type Foo struct { x int y int} end import xyz ", 1, "import must be before type or value statements");
    }
    
    @Test
    public void testT321() {
        useMockImportLoader = true;
        String src1 = "package xyz import abc import def type Foo int end let x Foo = 45";
        DValue dval = chkValue("xyz.x", src1, 1, 1);
        assertEquals(45, dval.asInt());
    }

    @Test
    public void testT400() {
        useMockImportLoader = false;
        String src1 = "import com.foo.package1 type Coord int end let origin Coord = 55";
        DValue dval = chkValue("origin", src1, 3, 3);
        assertEquals(55, dval.asInt());
        dval = findValue("com.foo.z");
        assertEquals("BLUE", dval.asString());
        dval = findValue("com.foo.core.ser");
        assertEquals("a55", dval.asString());
    }
    
    
}
