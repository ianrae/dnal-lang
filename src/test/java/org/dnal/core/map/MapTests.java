package org.dnal.core.map;

import static org.junit.Assert.*;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.impl.DataSetImpl;
import org.dnal.api.systest.SysTestBase;
import org.dnal.core.BuiltInTypes;
import org.dnal.core.DMapType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.builder.IntBuilder;
import org.dnal.core.builder.MapBuilder;
import org.junit.Test;

public class MapTests extends SysTestBase {

	@Test
	public void test0() {
		DataSet ds = createEmptyDataSet();
		Transaction trans = ds.createTransaction();
		
		IntBuilder builder = trans.createIntBuilder();
		DValue dval = builder.buildFrom(33);
		trans.add("z", dval);
		boolean b = trans.commit();
		assertEquals(true, b);
		assertEquals(1, ds.size());
	}
	
	@Test
	public void test() {
		DataSet ds = createEmptyDataSet();
		Transaction trans = ds.createTransaction();
		
		IntBuilder builder = trans.createIntBuilder();
		DValue dval = builder.buildFrom(33);
		
		//hack hack
		DataSetImpl dsimpl = (DataSetImpl) ds;
		DType elType = dsimpl.getInternals().getRegistry().getType(BuiltInTypes.INTEGER_SHAPE);
		DMapType mapType = new DMapType(Shape.MAP, "SizeMap", null, elType);
		dsimpl.getInternals().getRegistry().add("SizeMap", mapType);
		
		MapBuilder mapBuilder = trans.createMapBuilder("SizeMap");
		mapBuilder.addElement("key1", dval);
		mapBuilder.addElement("key2", builder.buildFrom(34));
		DValue mapDVal = mapBuilder.finish();
		
		trans.add("z", mapDVal);
		boolean b = trans.commit();
		assertEquals(true, b);
		assertEquals(1, ds.size());

		Integer n1 = mapDVal.asMap().get("key1").asInt();
		assertEquals(33, n1.intValue());
		Integer n2 = mapDVal.asMap().get("key2").asInt();
		assertEquals(33, n1.intValue());
	}
	
	@Test
	public void testTypeParse() {
		this.load("type SizeMap map<int> end", true);
		DataSet ds = dataSetLoaded;
		Transaction trans = ds.createTransaction();
		
		IntBuilder builder = trans.createIntBuilder();
		DValue dval = builder.buildFrom(33);
		
		MapBuilder mapBuilder = trans.createMapBuilder("SizeMap");
		mapBuilder.addElement("key1", dval);
		mapBuilder.addElement("key2", builder.buildFrom(34));
		DValue mapDVal = mapBuilder.finish();
		
		trans.add("z", mapDVal);
		boolean b = trans.commit();
		assertEquals(true, b);
		assertEquals(1, ds.size());

		Integer n1 = mapDVal.asMap().get("key1").asInt();
		assertEquals(33, n1.intValue());
		Integer n2 = mapDVal.asMap().get("key2").asInt();
		assertEquals(33, n1.intValue());
	}
	
	@Test
	public void testValueParse() {
		this.load("type SizeMap map<int> end let z SizeMap = { x:33, y:34 }", true);
		DataSet ds = dataSetLoaded;
		Transaction trans = ds.createTransaction();
		
		assertEquals(1, ds.size());
		DValue mapDVal = ds.getValue("z");
		Integer n1 = mapDVal.asMap().get("x").asInt();
		assertEquals(33, n1.intValue());
		Integer n2 = mapDVal.asMap().get("y").asInt();
		assertEquals(33, n1.intValue());
		
	}

	//---
	private DataSet createEmptyDataSet() {
		this.load("", true);
		DataSet ds = this.dataSetLoaded;
		assertEquals(0, ds.size());
		return ds;
	}
}
