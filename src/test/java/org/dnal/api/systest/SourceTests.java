package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;

import org.dnal.api.DNALCompiler;
import org.dnal.core.DValue;
import org.junit.Test;

/*
 */

public class SourceTests extends SysTestBase {

    @Test
    public void test() throws Exception {
    	String src = "type User struct { firstName string, lastName string optional } end let x string = 'abc'";
        DNALCompiler compiler = createCompiler();
       	dataSetLoaded = compiler.compileString(src);
       	assertEquals(0, compiler.getErrors().size());
       	DValue dval = dataSetLoaded.getValue("x");
       	assertEquals("abc", dval.asString());
    }
	
    @Test
    public void testSyntaxError() throws Exception {
    	//missing comma
    	String src = "type User struct { firstName string lastName string optional } end let x string = 'abc'";
        DNALCompiler compiler = createCompiler();
       	dataSetLoaded = compiler.compileString(src);
       	assertEquals(1, compiler.getErrors().size());
       	log(compiler.formatError(compiler.getErrors().get(0)));
    }

	//---
	private String dnal = "";
	
	private void add(String s) {
		dnal += s;
	}
}