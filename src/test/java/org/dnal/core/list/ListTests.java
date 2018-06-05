package org.dnal.core.list;

import static org.junit.Assert.*;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.systest.SysTestBase;
import org.dnal.core.DValue;
import org.junit.Test;

public class ListTests extends SysTestBase {

	@Test
	public void testList() {
		String src1 = "type Person struct { x string, y string } end ";
		src1 += "let joe Person = { 'aa', 'bb' } ";
		src1 += "let people list<Person> = [ joe, joe ] ";
		DValue list = compileValue(src1, "people", 2);
		DValue x = list.asList().get(0);
		DValue x2 = list.asList().get(0);
		assertEquals("aa", x.asStruct().getField("x").asString());
		assertEquals("bb", x.asStruct().getField("y").asString());
		assertEquals("aa", x2.asStruct().getField("x").asString());
		assertEquals("bb", x2.asStruct().getField("y").asString());
		
		//since both list elements are same dval, are actually same object
		assertSame(x, x2);
	}
	@Test
	public void testListAny() {
		String src1 = "type Person struct { x string, y string } end ";
		src1 += "let joe Person = { 'aa', 'bb' } ";
		src1 += "type AnyList list<any> end ";
		src1 += "let people AnyList = [ joe, joe ] ";
		DValue list = compileValue(src1, "people", 2);
		DValue x = list.asList().get(0);
		DValue x2 = list.asList().get(0);
		assertEquals("aa", x.asStruct().getField("x").asString());
		assertEquals("bb", x.asStruct().getField("y").asString());
		assertEquals("aa", x2.asStruct().getField("x").asString());
		assertEquals("bb", x2.asStruct().getField("y").asString());
		
		//since both list elements are same dval, are actually same object
		assertSame(x, x2);
	}
	@Test
	public void testListAny2() {
		String src1 = "type Person struct { x string, y string } end ";
		src1 += "let joe Person = { 'aa', 'bb' } ";
		src1 += "let people list<any> = [ joe, joe ] ";
		DValue list = compileValue(src1, "people", 2);
		DValue x = list.asList().get(0);
		DValue x2 = list.asList().get(0);
		assertEquals("aa", x.asStruct().getField("x").asString());
		assertEquals("bb", x.asStruct().getField("y").asString());
		assertEquals("aa", x2.asStruct().getField("x").asString());
		assertEquals("bb", x2.asStruct().getField("y").asString());
		
		//since both list elements are same dval, are actually same object
		assertSame(x, x2);
	}

	//---
	private DValue compileValue(String source, String varName, int expected) {
		this.load(source, true);
		DataSet ds = dataSetLoaded;
		Transaction trans = ds.createTransaction();
		
		assertEquals(expected, ds.size());
		DValue mapDVal = ds.getValue(varName);
		return mapDVal;
	}
}
