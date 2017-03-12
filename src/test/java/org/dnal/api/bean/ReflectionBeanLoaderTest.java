package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.DataSet;
import org.dnal.api.bean.DNALLoader;
import org.dnal.api.bean.ReflectionBeanLoader;
import org.dnal.core.DValue;
import org.junit.Test;


public class ReflectionBeanLoaderTest {
	
	public static class ClassA {
		private int nval;
		private long lval;
		private double dval;
		private boolean bval;
		private String eval;
		private String sval;
		
		public int getNval() {
			return nval;
		}
		public void setNval(int nval) {
			this.nval = nval;
		}
		public long getLval() {
			return lval;
		}
		public void setLval(long lval) {
			this.lval = lval;
		}
		public double getDval() {
			return dval;
		}
		public void setDval(double dval) {
			this.dval = dval;
		}
		public boolean isBval() {
			return bval;
		}
		public void setBval(boolean bval) {
			this.bval = bval;
		}
		public String getEval() {
			return eval;
		}
		public void setEval(String eval) {
			this.eval = eval;
		}
		public String getSval() {
			return sval;
		}
		public void setSval(String sval) {
			this.sval = sval;
		}
	}
	public static class ClassB {
		private long nval;
		public long getNval() {
			return nval;
		}

		public void setNval(long nval) {
			this.nval = nval;
		}

	}
	public static class ClassC {
		private boolean nval;
		public boolean getNval() {
			return nval;
		}

		public void setNval(boolean nval) {
			this.nval = nval;
		}

	}
	public static class ClassL {
		private List<Integer> numbers;
		
		public List<Integer> getNumbers() {
			return numbers;
		}

		public void setNumbers(List<Integer> number) {
			this.numbers = number;
		}
	}
	
	@Test
	public void test() {
        log("dnal..");
        DNALLoader dnalLoader = new DNALLoader();
        String dnalPath = buildTestPath("allTypes.dnal");
        boolean b = dnalLoader.loadTypeDefinition(dnalPath);
        dnalLoader.dumpErrors();
        assertEquals(true, b);
        
        DataSet ds = dnalLoader.getDataSet();
        ReflectionBeanLoader loader = new ReflectionBeanLoader("AllTypes", ds, dnalLoader.getErrorTracker(), null);
        
        ClassA a = new ClassA();
        a.setNval(15);
        a.setLval(190L);
        a.setDval(2.5);
        a.setBval(true);
        a.setEval("GREEN");
        a.setSval("abc");
        DValue dval = loader.createDValue(a);
        dnalLoader.dumpErrors();
        assertEquals(15, dval.asStruct().getField("nval").asInt());
        assertEquals(190, dval.asStruct().getField("lval").asLong());
        assertEquals(190, dval.asStruct().getField("lval").asLong());
        assertEquals(190, dval.asStruct().getField("lval").asLong());
        assertEquals("GREEN", dval.asStruct().getField("eval").asString());
        assertEquals("abc", dval.asStruct().getField("sval").asString());
        
        DValue ddd = dval.asStruct().getField("dval");
        assertEquals(2.5, ddd.asNumber(), 0.01);
        assertEquals(true, dval.asStruct().getField("bval").asBoolean());
        
//        ClassB bb = new ClassB();
//        bb.setNval(150L);
//        dval = loader.createDValue(bb);
//        assertEquals(150, dval.asStruct().getField("nval").asLong());
	}
	@Test
	public void test0() {
        log("dnal..");
        DNALLoader dnalLoader = new DNALLoader();
        boolean b = dnalLoader.loadTypeDefinitionFromString("type Z struct { numbers list<int> } end");
        dnalLoader.dump();
        assertEquals(true, b);
        
        DataSet ds = dnalLoader.getDataSet();
        ReflectionBeanLoader loader = new ReflectionBeanLoader("Z", ds, dnalLoader.getErrorTracker(), null);
        
        ClassL a = new ClassL();
        List<Integer> list = new ArrayList<>();
        list.add(23);
        list.add(25);
        a.setNumbers(list);
        DValue dval = loader.createDValue(a);
        
        List<DValue> list2 = dval.asStruct().getField("numbers").asList();
        assertEquals(2, list2.size());
        assertEquals(23, list2.get(0).asInt());
        assertEquals(25, list2.get(1).asInt());
	}
	
	@Test
	public void testFail() {
        DNALLoader dnalLoader = new DNALLoader();
        String dnalPath = buildTestPath("allTypes.dnal");
        boolean b = dnalLoader.loadTypeDefinition(dnalPath);
        assertEquals(true, b);
        
        DataSet ds = dnalLoader.getDataSet();
        ReflectionBeanLoader loader = new ReflectionBeanLoader("AllTypes", ds, dnalLoader.getErrorTracker(), null);
        ClassC c = new ClassC();
        c.setNval(true);
        DValue dval = loader.createDValue(c);
        dnalLoader.getErrorTracker().dumpErrors();
        assertNull(dval);
	}
	
	//---
	private static final String BEAN_TESTFILE_DIR = "./src/main/resources/test/bean/";
	
    private String buildTestPath(String partialPath) {
        String path = BEAN_TESTFILE_DIR + partialPath;
        return path;
    }
	
	private void log(String s) {
		System.out.println(s);;
	}

}
