package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;

import org.dnal.api.DNALCompiler;
import org.dnal.core.DValue;
import org.junit.Test;

/*
 */

public class ListTests extends SysTestBase {

    @Test
    public void test() throws Exception {
    	String src = "type User struct { firstName string } end ";
    	src += " let x list<User> = [ {'bob'}, {'sue'} {'art'} ]";  //missing comma after sue
        DNALCompiler compiler = createCompiler();
       	dataSetLoaded = compiler.compileString(src);
       	assertEquals(0, compiler.getErrors().size());
       	DValue dval = dataSetLoaded.getValue("x");
       	assertEquals(2, dval.asList().size());
       	assertEquals("bob", dval.asList().get(0).asStruct().getField("firstName").asString());
       	assertEquals("sue", dval.asList().get(1).asStruct().getField("firstName").asString());
       	chkList(dval, 0, "bob");
    }
	
	//---
	private String dnal = "";
	
	private void add(String s) {
		dnal += s;
	}
	
	private void chkList(DValue dval, int index, String expected) {
       	assertEquals(expected, dval.asList().get(index).asStruct().getField("firstName").asString());
	}
}