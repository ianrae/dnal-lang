package org.dnal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.dnal.core.repository.MockRepositoryFactory;
import org.dnal.core.xbuilder.XIntegerValueBuilder;
import org.dnal.core.xbuilder.XListValueBuilder;
import org.dnal.core.xbuilder.XNumberValueBuilder;
import org.dnal.core.xbuilder.XStringValueBuilder;
import org.dnal.core.xbuilder.XStructValueBuilder;
import org.junit.Test;

public class DValTests extends BaseDValTest {

	@Test
	public void test() {
		Shape shape = Shape.INTEGER;
		Shape shape2 = Shape.STRING;
		assertEquals(false, shape.equals(shape2));
		log(shape.toString());
	}

	@Test
	public void testRegistry() {
		world.setRepositoryFactory(new MockRepositoryFactory());
		DTypeRegistryBuilder regBuilder = new DTypeRegistryBuilder();
		regBuilder.init(world);
		DTypeRegistry myRegistry = regBuilder.getRegistry();
		int numBuiltIns = 7;
		assertEquals(numBuiltIns, myRegistry.size());

		assertEquals(numBuiltIns, myRegistry.getAll().size());
		String name = BuiltInTypes.INTEGER_SHAPE.name();
		DType type = myRegistry.getType(name);
		assertEquals(name, type.getName());
		assertNull(myRegistry.getType("nosuchtype"));
	}

	@Test
	public void testDValueInt() {
		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
		Long nval = Long.valueOf(33);

		DValue val = new DValueImpl(type, nval);
		assertEquals(type, val.getType());

		Long nval2 = (Long) val.getObject();
		assertEquals(33, nval2.intValue());
		assertEquals(33, val.asInt());
		assertTrue(val.getType()== type);
	}
    @Test
    public void testDValueNumber() {
        DType type = registry.getType(BuiltInTypes.NUMBER_SHAPE);
        Double d = Double.valueOf(33.2);

        DValue val = new DValueImpl(type, d);
        assertEquals(type, val.getType());

        Double nval2 = (Double) val.getObject();
        assertEquals(33.2, nval2.doubleValue(), 0.0001);
        assertTrue(val.getType()== type);
    }
	@Test
	public void testDValueStr() {
		DType type = registry.getType(BuiltInTypes.STRING_SHAPE);
		String s = "abc";

		DValue val = new DValueImpl(type, s);
		assertEquals(type, val.getType());

		String s2 = (String) val.getObject();
		assertEquals("abc", s2);
		assertEquals("abc", val.asString());
		assertTrue(val.getType()== type);
	}

	@Test
	public void testIntBuilder() {
		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
		XIntegerValueBuilder builder = new XIntegerValueBuilder(type);

		builder.buildFromString("123");
		assertEquals(false, builder.wasSuccessful());
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		for(NewErrorMessage err: builder.getValidationErrors()) {
			log(err.getMessage());
		}

		DValue dval = builder.getDValue();
		assertEquals(123, dval.asInt());
		assertTrue(dval.getType()== type);

		log("2..");
		builder = new XIntegerValueBuilder(type);
		builder.buildFromString("xyz");
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("3..");
		builder = new XIntegerValueBuilder(type);
		builder.buildFromString(null);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("4..");
		builder = new XIntegerValueBuilder(type);
		builder.buildFrom(100);
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		chkErrors(builder, 0);
		dval = builder.getDValue();
		assertEquals(100L, dval.asLong());
	}

    @Test
    public void testNumberBuilder() {
        DType type = registry.getType(BuiltInTypes.NUMBER_SHAPE);
        XNumberValueBuilder builder = new XNumberValueBuilder(type);

        builder.buildFromString("123.5");
        assertEquals(false, builder.wasSuccessful());
        builder.finish();
        assertEquals(true, builder.wasSuccessful());

        for(NewErrorMessage err: builder.getValidationErrors()) {
            log(err.getMessage());
        }

        DValue dval = builder.getDValue();
        assertEquals(123.5, dval.asNumber(), 0.0001);
        assertTrue(dval.getType()== type);

        log("2..");
        builder = new XNumberValueBuilder(type);
        builder.buildFromString("xyz");
        builder.finish();
        assertEquals(false, builder.wasSuccessful());

        chkErrors(builder, 1);
        dval = builder.getDValue();
        assertEquals(null, dval);

        log("3..");
        builder = new XNumberValueBuilder(type);
        builder.buildFromString(null);
        builder.finish();
        assertEquals(false, builder.wasSuccessful());

        chkErrors(builder, 1);
        dval = builder.getDValue();
        assertEquals(null, dval);

        log("4..");
        builder = new XNumberValueBuilder(type);
        builder.buildFrom(Double.valueOf(100.0));
        builder.finish();
        assertEquals(true, builder.wasSuccessful());

        chkErrors(builder, 0);
        dval = builder.getDValue();
        assertEquals(100.0, dval.asNumber(), 0.0001);
    }
	
	@Test
	public void testStrBuilder() {
		DType type = registry.getType(BuiltInTypes.STRING_SHAPE);
		XStringValueBuilder builder = new XStringValueBuilder(type);

		builder.buildFromString("123");
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		assertEquals("123", dval.asString());
		assertTrue(dval.getType()== type);

		log("3..");
		builder = new XStringValueBuilder(type);
		builder.buildFromString(null);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);
	}

	@Test
	public void testListBuilder() {
		DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
		DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
		registerType("mylist", type);
		
		XListValueBuilder builder = new XListValueBuilder(type);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		List<DValue> list = dval.asList();
		assertEquals(0, list.size());

		log("3..");
		DValue sval = buildStringVal(registry, "123");
		builder = new XListValueBuilder(type);
		builder.addValue(sval);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		dval = builder.getDValue();
		list = dval.asList();
		assertEquals(1, list.size());
		String s = list.get(0).asString();
		assertEquals("123", s);


		log("4..");
		DValue nval = buildIntVal(registry, 456);
		builder = new XListValueBuilder(type);
		builder.addValue(nval);

		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);
	}
	@Test
	public void testStructBuilder() {
		DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
        OrderedMap fieldMap = new OrderedMap();
		fieldMap.add("field1", eltype, false, false);
		fieldMap.add("field2", eltype, true, false);
		DStructType type = new DStructType(Shape.STRUCT, "mylist", null, fieldMap);
		registerType("mylist", type);

		XStructValueBuilder builder = new XStructValueBuilder(type);
		builder.addField("field1", buildStringVal(registry, "123"));
		builder.addField("field2", buildStringVal(registry, "123"));
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		Map<String,DValue> map = dval.asMap();
		assertEquals(2, map.size());
		assertEquals(false, type.fieldIsOptional("field1"));
		assertEquals(true, type.fieldIsOptional("field2"));
		
		log("2..");
		builder = new XStructValueBuilder(type);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);
		
		log("3..");
		builder = new XStructValueBuilder(type);
		builder.addField("fieldx", buildStringVal(registry, "123"));
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);

		log("4..");
		builder = new XStructValueBuilder(type);
		builder.addField("", buildStringVal(registry, "123"));
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);

		log("5..");
		builder = new XStructValueBuilder(type);
		builder.addField("field1", buildIntVal(registry, 444));
		builder.addField("field2", buildStringVal(registry, "123"));
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);
	}

	//-----
}
