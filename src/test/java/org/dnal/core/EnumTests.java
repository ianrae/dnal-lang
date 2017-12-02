package org.dnal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.dnal.compiler.parser.ast.EnumMemberExp;
import org.dnal.compiler.parser.ast.FullEnumTypeExp;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.fluent.type.TypeBuilder;
import org.dnal.core.fluent.type.TypeBuilder.Inner;
import org.dnal.core.xbuilder.XEnumValueBuilder;
import org.dnal.core.xbuilder.XListValueBuilder;
import org.dnal.core.xbuilder.XStructValueBuilder;
import org.junit.Test;

public class EnumTests extends BaseDValTest {

	@Test
	public void testEnum() {
		DType type = registry.getType(BuiltInTypes.ENUM_SHAPE);
		String nval = "BLUE";

		DValue val = new DValueImpl(type, nval);
		assertEquals(type, val.getType());

		String nval2 = (String) val.getObject();
		assertEquals("BLUE", nval2);
		assertTrue(val.getType()== type);
	}

	@Test
	public void testBuilder() {
		DStructType enumType = buildColourEnumType(registry);
		XEnumValueBuilder builder = new XEnumValueBuilder(enumType);

		builder.buildFromString("RED");
		assertEquals(false, builder.wasSuccessful());
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		for(NewErrorMessage err: builder.getValidationErrors()) {
			log(err.getMessage());
		}

		DValue dval = builder.getDValue();
		assertEquals("RED", dval.asString());
		assertTrue(dval.getType()== enumType);

		log("2..");
		builder = new XEnumValueBuilder(enumType);
		builder.buildFromString("GREEN");
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		chkErrors(builder, 0);
		dval = builder.getDValue();

		log("3..");
		builder = new XEnumValueBuilder(enumType);
		builder.buildFromString(null);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("4..");
		builder = new XEnumValueBuilder(enumType);
		builder.buildFromString("BLUE");
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		chkErrors(builder, 0);
		dval = builder.getDValue();
		assertEquals("BLUE", dval.asString());
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
		DStructType enumType = buildColourEnumType(registry);
		DStructType type = this.buildSettingsType(registry);

		XStructValueBuilder builder = new XStructValueBuilder(type);
		builder.addField("flag1", buildBooleanVal(registry, true));
		builder.addField("flag2", buildEnumVal(registry, enumType, "RED"));
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		Map<String,DValue> map = dval.asMap();
		assertEquals(2, map.size());
		DStructHelper helper= dval.asStruct();
		assertEquals(true, helper.getField("flag1").asBoolean());
		assertEquals("RED", helper.getField("flag2").asString());
		
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
		.enumeration("flag2", "Colour")
		.end();

		DStructType type = tb.getType();
		return type;
	}
	
	
}
