package org.dval;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.dval.fluent.type.TypeBuilder;
import org.dval.oldbuilder.XListValueBuilder;
import org.dval.oldbuilder.XStructValueBuilder;
import org.junit.Test;

public class NumberTests extends BaseDValTest {

	@Test
	public void testNumber() {
		DType type = registry.getType(BuiltInTypes.NUMBER_SHAPE);
		assertNotNull(type);
		Boolean nval = Boolean.TRUE;

		DValue val = new DValueImpl(type, nval);
		assertEquals(type, val.getType());

		Boolean nval2 = (Boolean) val.getObject();
		assertEquals(true, nval2.booleanValue());
		assertTrue(val.getType()== type);
	}

	
	@Test
	public void testListBuilder() {
		DType eltype = registry.getType(BuiltInTypes.NUMBER_SHAPE);
		DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
		registerType("mylist", type);
		
		XListValueBuilder builder = new XListValueBuilder(type);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		List<DValue> list = dval.asList();
		assertEquals(0, list.size());

		log("3..");
		DValue sval = buildNumberVal(registry, 100.5);
		builder = new XListValueBuilder(type);
		builder.addValue(sval);

		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		dval = builder.getDValue();
		list = dval.asList();
		assertEquals(1, list.size());
		String s = list.get(0).asString();
		assertEquals("100.5", s);


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
		DStructType type = this.buildDimmensionType(registry);

		XStructValueBuilder builder = new XStructValueBuilder(type);
		builder.addField("width", buildNumberVal(registry, 100.5));
		builder.addField("height", buildNumberVal(registry, 99.0));
		builder.finish();
		this.dumpErrors(builder);
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		Map<String,DValue> map = dval.asMap();
		assertEquals(2, map.size());
		DStructHelper helper= dval.asStruct();
		assertEquals(100.5, helper.getField("width").asNumber(), 0.001);
		assertEquals(99.0, helper.getField("height").asNumber(), 0.001);
		
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
    private DStructType buildDimmensionType(DTypeRegistry registry) {
        TypeBuilder tb = new TypeBuilder(registry, world);
        tb.start("Settings")
        .number("width")
        .number("height")
        .end();

        DStructType type = tb.getType();
        return type;
    }
}
