package org.dval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.dval.fluent.type.TypeBuilder;
import org.dval.oldbuilder.XBooleanValueBuilder;
import org.dval.oldbuilder.XListValueBuilder;
import org.dval.oldbuilder.XStructValueBuilder;
import org.junit.Test;

public class BooleanTests extends BaseDValTest {

	@Test
	public void testBoolean() {
		DType type = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
		Boolean nval = Boolean.TRUE;

		DValue val = new DValueImpl(type, nval);
		assertEquals(type, val.getType());

		Boolean nval2 = (Boolean) val.getObject();
		assertEquals(true, nval2.booleanValue());
		assertTrue(val.getType()== type);
	}

	@Test
	public void testBuilder() {
		DType type = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
		XBooleanValueBuilder builder = new XBooleanValueBuilder(type);

		builder.buildFromString("true");
		assertEquals(false, builder.wasSuccessful());
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		for(ErrorMessage err: builder.getValidationErrors()) {
			log(err.getMessage());
		}

		DValue dval = builder.getDValue();
		assertEquals(true, dval.asBoolean());
		assertTrue(dval.getType()== type);

		log("2..");
		builder = new XBooleanValueBuilder(type);
		builder.buildFromString("xyz");
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("3..");
		builder = new XBooleanValueBuilder(type);
		builder.buildFromString(null);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("4..");
		builder = new XBooleanValueBuilder(type);
		builder.buildFrom(Boolean.TRUE);
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		chkErrors(builder, 0);
		dval = builder.getDValue();
		assertEquals(true, dval.asBoolean());
	}
	
	@Test
	public void testListBuilder() {
		DType eltype = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
		DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
        registerType("mylist", type);
		
		XListValueBuilder builder = new XListValueBuilder(type);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		List<DValue> list = dval.asList();
		assertEquals(0, list.size());

		log("3..");
		DValue sval = buildBooleanVal(registry, true);
		builder = new XListValueBuilder(type);
		builder.addValue(sval);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		dval = builder.getDValue();
		list = dval.asList();
		assertEquals(1, list.size());
		String s = list.get(0).asString();
		assertEquals("true", s);


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
		builder.addField("flag1", buildBooleanVal(registry, true));
		builder.addField("flag2", buildBooleanVal(registry, false));
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		Map<String,DValue> map = dval.asMap();
		assertEquals(2, map.size());
		DStructHelper helper= dval.asStruct();
		assertEquals(true, helper.getField("flag1").asBoolean());
		assertEquals(false, helper.getField("flag2").asBoolean());
		
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
		.bool("flag1")
		.bool("flag2")
		.end();

		DStructType type = tb.getType();
		return type;
	}
}
