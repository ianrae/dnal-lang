package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import org.dnal.api.bean.ValidationProcessor;
import org.dnal.api.bean.ValidationResult;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.DValue;
import org.junit.Test;

public class ValidationProcessorTests {
	
	public static class ClassBB {
		private long nval;
		public long getNval() {
			return nval;
		}

		public void setNval(long nval) {
			this.nval = nval;
		}
	}
	public static class ClassCC {
		private long pval;

		public long getPval() {
			return pval;
		}

		public void setPval(long pval) {
			this.pval = pval;
		}
	}
	
	@Test
	public void test0() throws Exception {
        ValidationProcessor processor = new ValidationProcessor();
        boolean b = processor.loadTypeDefinitionFromString("type Z struct { nval long } nval < 1000 end");
        assertEquals(true, b);
        
        XErrorTracker.logErrors = true;
        
        ClassBB bean = new ClassBB();
        bean.setNval(400L);
        ValidationResult result = processor.createFromBean("Z", bean);
        assertEquals(true, result.succeeded());
        DValue dval = result.getDval();
        assertEquals(400L, dval.asStruct().getField("nval").asLong());
        
        bean = new ClassBB();
        bean.setNval(401L);
        result = processor.createFromBean("Z", bean);
        assertEquals(true, result.succeeded());
        dval = result.getDval();
        assertEquals(401L, dval.asStruct().getField("nval").asLong());
	}
	
	@Test
	public void testFail() throws Exception {
        ValidationProcessor processor = new ValidationProcessor();
        boolean b = processor.loadTypeDefinitionFromString("type Z struct { nval long } end");
        assertEquals(true, b);
        
        ClassCC bean = new ClassCC();
        bean.setPval(400L);
        ValidationResult result = processor.createFromBean("Z", bean);
        assertEquals(false, result.succeeded());
        assertEquals(null, result.getDval());
        
        log("errors..");
        processor.dumpErrors();
	}
	
	@Test
	public void testFail2() throws Exception {
        ValidationProcessor processor = new ValidationProcessor();
        boolean b = processor.loadTypeDefinitionFromString("type Z struct { nval long } nval < 100 end");
        assertEquals(true, b);
        
        ClassBB bean = new ClassBB();
        bean.setNval(401L);
        ValidationResult result = processor.createFromBean("Z", bean);
        assertEquals(false, result.succeeded());
        assertEquals(null, result.getDval());
        
        log("2nd..");
        bean = new ClassBB();
        bean.setNval(401L);
        result = processor.createFromBean("Z", bean);
        assertEquals(false, result.succeeded());
        assertEquals(null, result.getDval());
        
        log("errors..");
        processor.dumpErrors();
	}
	
	private void log(String s) {
		System.out.println(s);;
	}

}
