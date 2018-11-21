package org.dnal.api.systest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.builder.StructBuilder;
import org.junit.Test;

public class UniqueRuleTests extends SysTestBase {

    @Test
    public void testUniqueInTrans() {
    	String src = buildDNAL(99, 100, 101);
    	DataSet ds = load(src, true);
    	
    	Transaction trans = ds.createTransaction();
    	StructBuilder builder = trans.createStructBuilder("Foo");
    	builder.addField("x", "99");
    	DValue dval = builder.finish();
    	assertNotNull(dval);
    	
    	trans.add("x33", dval);
    	boolean b = trans.commit();
    	assertEquals(false, b);  //should fail
    	
        this.registry.dump();
        this.world.dump();
    }

    @Test
    public void testUniqueInTransClone() {
    	String src = buildDNAL(99, 100, 101);
    	DataSet ds1 = load(src, true);
    	assertEquals(1, ds1.size());
    	DataSet ds = ds1.cloneDataSet();
    	assertEquals(1, ds.size());
    	
    	Transaction trans = ds.createTransaction();
    	StructBuilder builder = trans.createStructBuilder("Foo");
    	builder.addField("x", "99");
    	DValue dval = builder.finish();
    	assertNotNull(dval);
    	
    	trans.add("x33", dval);
    	boolean b = trans.commit();
    	assertEquals(false, b);
    	assertEquals(1, ds.size());
    	assertEquals(1, ds1.size());
    	
        this.registry.dump();
        this.world.dump();
    }
    
    @Test
    public void testUniqueInTransCloneOK() {
    	String src = buildDNAL(99, 100, 101);
    	DataSet ds1 = load(src, true);
    	assertEquals(1, ds1.size());
    	DataSet ds = ds1.cloneDataSet();
    	assertEquals(1, ds.size());
    	
    	Transaction trans = ds.createTransaction();
    	StructBuilder builder = trans.createStructBuilder("Foo");
    	builder.addField("x", "102");
    	DValue dval = builder.finish();
    	assertNotNull(dval);
    	
    	trans.add("x33", dval);
    	boolean b = trans.commit();
    	assertEquals(true, b);
    	assertEquals(2, ds.size());
    	assertEquals(1, ds1.size());
    	
        this.registry.dump();
        this.world.dump();
    }

    //-----------------------
	private String buildDNAL(int n1, int n2, int n3) {
		String dnal = String.format("type Foo struct { x int unique} end let x list<Foo> = [{ %d }, {%d}, {%d} ]", n1, n2, n3);
		return dnal;
	}
}
