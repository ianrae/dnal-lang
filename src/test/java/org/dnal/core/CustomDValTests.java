package org.dnal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dnal.core.BuiltInTypes;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.OrderedMap;
import org.dnal.core.Shape;
import org.dnal.core.oldbuilder.XIntegerValueBuilder;
import org.dnal.core.oldbuilder.XListValueBuilder;
import org.dnal.core.oldbuilder.XStructValueBuilder;
import org.junit.Test;

public class CustomDValTests extends BaseDValTest {


	@Test
	public void testDValueInt() {
		regMyInt(registry);
		DType type = registry.getType("MyInt");
		Long nval = Long.valueOf(33);

		DValue val = new DValueImpl(type, nval);
		assertEquals(type, val.getType());

		Long nval2 = (Long) val.getObject();
		assertEquals(33, nval2.intValue());
		assertEquals(33, val.asInt());
		assertTrue(val.getType()== type);
	}
	@Test
	public void testListBuilder() {
		regMyInt(registry);
		DType eltype = registry.getType("MyInt");
		DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
		registerType("mylist", type);
		
		DValue sval = buildCustomIntVal(registry, 123, "MyInt");
		XListValueBuilder builder = new XListValueBuilder(type);
		builder.addValue(sval);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		List<DValue>list = dval.asList();
		assertEquals(1, list.size());
		Long lval = list.get(0).asLong();
		assertEquals(123L, lval.longValue());


		log("4..");
		DValue nval = buildIntVal(registry, 456);
		builder = new XListValueBuilder(type);
		builder.addValue(nval);

		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);
	}
	@Test
	public void testListInheritance() {
		regMyInt(registry);
		DType eltype = registry.getType(BuiltInTypes.INTEGER_SHAPE);
		DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
		registerType("mylist", type);
		
		DValue sval = this.buildCustomIntVal(registry, 123, "MyInt");
		XListValueBuilder builder = new XListValueBuilder(type);
		builder.addValue(sval);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		List<DValue>list = dval.asList();
		assertEquals(1, list.size());
		Long lval = list.get(0).asLong();
		assertEquals(123L, lval.longValue());
	}
	@Test
	public void testListInheritance2() {
		regMyInt(registry);
		DType eltype = registry.getType("MyInt");
		DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
		
		DValue sval = this.buildIntVal(registry, 123);
		XListValueBuilder builder = new XListValueBuilder(type);
		builder.addValue(sval);

		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);
	}
	
	@Test
	public void testStructInheritance() {
		regMyInt(registry);
		DType eltype = registry.getType(BuiltInTypes.INTEGER_SHAPE);
        OrderedMap fieldMap = new OrderedMap();
		fieldMap.add("field1", eltype, false, false);
		fieldMap.add("field2", eltype, false, false);
		DStructType type = new DStructType(Shape.STRUCT, "mylist", null, fieldMap);
		registerType("mylist", type);
		XStructValueBuilder builder = new XStructValueBuilder(type);
		DValue sval = this.buildCustomIntVal(registry, 123, "MyInt");
		builder.addField("field1", sval);
		sval = this.buildCustomIntVal(registry, 123, "MyInt");
		builder.addField("field2", sval);
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		Map<String,DValue> map = dval.asMap();
		assertEquals(2, map.size());
		
	}
	//-----
	private void regMyInt(DTypeRegistry registry) {
		String name = "MyInt";
		DType baseType = registry.getType(BuiltInTypes.INTEGER_SHAPE);
		DType type = new DType(Shape.INTEGER, name, baseType);
		registerType(name, type);
	}
	protected DValue buildCustomIntVal(DTypeRegistry registry, int n, String typeName) {
		DType type = registry.getType(typeName);
		XIntegerValueBuilder builder = new XIntegerValueBuilder(type);
		builder.buildFrom(n);
		assertEquals(true, builder.finish());
		return builder.getDValue();
	}
	
}
