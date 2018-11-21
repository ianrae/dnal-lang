package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.dnal.api.DNALCompiler;
import org.dnal.api.Transaction;
import org.dnal.api.impl.CompilerImpl;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.DValue;
import org.dnal.core.builder.StructBuilder;
import org.junit.Test;

/*
 */

public class TopLevelTests extends SysTestBase {

    @Test
    public void testSource() throws Exception {
    	String src = "type User struct { firstName string } end ";
    	src += " let x User = {'bob'} ";  
    	src += " let x User = {'sue'} ";  
        DNALCompiler compiler = createCompiler();
       	dataSetLoaded = compiler.compileString(src);
       	assertEquals(1, compiler.getErrors().size());
       	assertEquals(null, dataSetLoaded);

       	CompilerImpl cimpl = (CompilerImpl) compiler;
       	cimpl.getContext().et.dumpErrors();
    }
    
    @Test
    public void testTrans() throws Exception {
    	String src = "type User struct { firstName string } end ";
    	src += " let x User = {'bob'} ";  
        DNALCompiler compiler = createCompiler();
       	dataSetLoaded = compiler.compileString(src);
       	assertEquals(0, compiler.getErrors().size());
       	assertNotNull(dataSetLoaded.getValue("x"));

       	CompilerImpl cimpl = (CompilerImpl) compiler;
       	cimpl.getContext().et.dumpErrors();
       	
    	Transaction trans = dataSetLoaded.createTransaction();
    	StructBuilder builder = trans.createStructBuilder("User");
    	builder.addField("firstName", "sue");
    	DValue dval = builder.finish();
    	assertNotNull(dval);
    	
    	trans.add("x", dval);
    	boolean b = trans.commit();
    	assertEquals(false, b);  //should fail

    }
	
	//---
}