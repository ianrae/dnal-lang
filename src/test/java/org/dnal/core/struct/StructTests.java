package org.dnal.core.struct;

import static org.junit.Assert.assertEquals;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.systest.SysTestBase;
import org.dnal.core.DValue;
import org.junit.Test;

public class StructTests extends SysTestBase {

	@Test
	public void testStructNoFieldName() {
		String src1 = "type Person struct { x string, y string } end ";
		src1 += "let joe Person = { 'aa', 'bb' } ";
		DValue x = compileValue(src1, "joe", 1);
		
		assertEquals("aa", x.asStruct().getField("x").asString());
		assertEquals("bb", x.asStruct().getField("y").asString());
	}
	@Test
	public void testStructWithFieldName() {
		String src1 = "type Person struct { x string, y string } end ";
		src1 += "let joe Person = { y:'bb', x:'aa' } ";
		DValue x = compileValue(src1, "joe", 1);
		
		assertEquals("aa", x.asStruct().getField("x").asString());
		assertEquals("bb", x.asStruct().getField("y").asString());
	}
	@Test
	public void testStructFieldNameDot() {
		String src1 = "type Person struct { x string, y string } end ";
		src1 += "let joe Person = { 'com.x':'aa', y:'bb' } ";
		chkFail(src1, 1, "does not contain field");
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
