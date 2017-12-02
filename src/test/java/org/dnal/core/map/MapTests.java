package org.dnal.core.map;

import static org.junit.Assert.*;

import java.util.List;

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
	
	//maps are limited. key is string. all values must be same type.
	//perhaps later support map<any>
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
		assertEquals(34, n2.intValue());
	}
	@Test
	public void testValueParseBoolean() {
		DValue mapDVal = compileSingleMapValue("type SizeMap map<boolean> end let z SizeMap = { x:false, y:true }", "z");
		Boolean n1 = mapDVal.asMap().get("x").asBoolean();
		assertEquals(false, n1.booleanValue());
		Boolean n2 = mapDVal.asMap().get("y").asBoolean();
		assertEquals(true, n2.booleanValue());
	}
	@Test
	public void testValueParseString() {
		DValue mapDVal = compileSingleMapValue("type SizeMap map<string> end let z SizeMap = { x:'abc' }", "z");
		String s = mapDVal.asMap().get("x").asString();
		assertEquals("abc", s);
	}
	@Test
	public void testValueParseList() {
		String src1 = "type NameList list<string> end ";
		DValue mapDVal = compileSingleMapValue(src1 + "type SizeMap map<NameList> end let z SizeMap = { x:['abc','def'] }", "z");
		List<DValue> list = mapDVal.asMap().get("x").asList();
		assertEquals(2, list.size());
		assertEquals("abc", list.get(0).asString());
		assertEquals("def", list.get(1).asString());
	}

	//---
	private DataSet createEmptyDataSet() {
		this.load("", true);
		DataSet ds = this.dataSetLoaded;
		assertEquals(0, ds.size());
		return ds;
	}
	
	private DValue compileSingleMapValue(String source, String varName) {
		this.load(source, true);
		DataSet ds = dataSetLoaded;
		Transaction trans = ds.createTransaction();
		
		assertEquals(1, ds.size());
		DValue mapDVal = ds.getValue(varName);
		return mapDVal;
	}
}
