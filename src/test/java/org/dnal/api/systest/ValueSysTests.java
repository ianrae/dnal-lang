package org.dnal.api.systest;

import org.junit.Test;

public class ValueSysTests extends SysTestBase {

    @Test
    public void testT200() {
        chkFail("type Foo int end let x Foo = 14 let x int = 44", 1, "value name 'x' has already been defined");
    }
    
    @Test
    public void testT201() {
        chkFail("let x int = 5 let y int = zzz", 1, "cannot resolve reference to 'zzz'");
    }
    
    @Test
    public void testT202() {
		String src1 = "type Address struct { street string, city string} end ";
		String src2 = "type Customer struct { firstName string, addr Address, age int} end ";
		String src3 = "let addr1 Address = { '10 Main st', 'Ottawa'}";
		String src4 = "let x Customer = { 'bobby', addr1, 44 }";
        chk(src1 + src2 + src3 + src4, 2, 2);
    }

    @Test
    public void testT202a() {
		String src1 = "type Address struct { street string, city string} end ";
		String src1a = "type Location struct { street string, city string} end ";
		String src2 = "type Customer struct { firstName string, addr Address, age int} end ";
		String src3 = "let addr1 Location = { '10 Main st', 'Ottawa'}";
		String src4 = "let x Customer = { 'bobby', addr1, 44 }";
        chkFail(src1 + src1a + src2 + src3 + src4, 1, "addr: cannot assign a value of type 'Location'");
    }

    @Test
    public void testT203() {
		String src1 = "type Address struct { street string, city string} end ";
		String src2 = "type Customer struct { firstName string, addr Address, age int} end ";
		String src3 = "let addr1 Address = { '10 Main st', 'Ottawa'}";
		String src4 = "let x Customer = { firstName:'bobby', addr:addr1, age:44 }";
        chk(src1 + src2 + src3 + src4, 2, 2);
    }
    
}