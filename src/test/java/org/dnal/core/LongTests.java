package org.dnal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.dnal.core.BuiltInTypes;
import org.dnal.core.DListType;
import org.dnal.core.DStructHelper;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.ErrorMessage;
import org.dnal.core.Shape;
import org.dnal.core.fluent.type.TypeBuilder;
import org.dnal.core.oldbuilder.XListValueBuilder;
import org.dnal.core.oldbuilder.XLongValueBuilder;
import org.dnal.core.oldbuilder.XStructValueBuilder;
import org.junit.Test;

public class LongTests extends BaseDValTest {

	@Test
	public void testLong() {
		DType type = registry.getType(BuiltInTypes.LONG_SHAPE);
		Long nval = 1400L;

		DValue val = new DValueImpl(type, nval);
		assertEquals(type, val.getType());

		Long nval2 = (Long) val.getObject();
		assertEquals(1400L, nval2.longValue());
		assertTrue(val.getType()== type);
	}

	@Test
	public void testBuilder() {
		DType type = registry.getType(BuiltInTypes.LONG_SHAPE);
		XLongValueBuilder builder = new XLongValueBuilder(type);

		builder.buildFromString("1400");
		assertEquals(false, builder.wasSuccessful());
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		for(NewErrorMessage err: builder.getValidationErrors()) {
			log(err.getMessage());
		}

		DValue dval = builder.getDValue();
		assertEquals(1400L, dval.asLong());
		assertTrue(dval.getType()== type);

		log("2..");
		builder = new XLongValueBuilder(type);
		builder.buildFromString("xyz");
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("3..");
		builder = new XLongValueBuilder(type);
		builder.buildFromString(null);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("4..");
		builder = new XLongValueBuilder(type);
		builder.buildFrom(14L);
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		chkErrors(builder, 0);
		dval = builder.getDValue();
		assertEquals(14L, dval.asLong());
	}
	
	@Test
	public void testListBuilder() {
		DType eltype = registry.getType(BuiltInTypes.LONG_SHAPE);
		DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
        registerType("mylist", type);
		
		XListValueBuilder builder = new XListValueBuilder(type);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		List<DValue> list = dval.asList();
		assertEquals(0, list.size());

		log("3..");
		DValue sval = buildLongVal(registry, 15L);
		builder = new XListValueBuilder(type);
		builder.addValue(sval);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		dval = builder.getDValue();
		list = dval.asList();
		assertEquals(1, list.size());
		String s = list.get(0).asString();
		assertEquals("15", s);


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
		DStructType type = this.buildSettingsType(registry);

		XStructValueBuilder builder = new XStructValueBuilder(type);
		builder.addField("flag1", buildLongVal(registry, 100L));
		builder.addField("flag2", buildLongVal(registry, 101L));
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		Map<String,DValue> map = dval.asMap();
		assertEquals(2, map.size());
		DStructHelper helper= dval.asStruct();
		assertEquals(100L, helper.getField("flag1").asLong());
		assertEquals(101L, helper.getField("flag2").asLong());
		
		log("2..");
		builder = new XStructValueBuilder(type);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 2);
	}

	//-----
	private DStructType buildSettingsType(DTypeRegistry registry) {
		TypeBuilder tb = new TypeBuilder(registry, world);
		tb.start("Settings")
		.longInteger("flag1")
		.longInteger("flag2")
		.end();

		DStructType type = tb.getType();
		return type;
	}
}
