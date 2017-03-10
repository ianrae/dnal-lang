package org.dnal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dnal.core.oldbuilder.XDateValueBuilder;
import org.dnal.core.oldbuilder.XStructValueBuilder;
import org.junit.Test;

public class DateTests extends BaseDValTest {

	@Test
	public void testDValueDate() {
		DType type = registry.getType(BuiltInTypes.DATE_SHAPE);
		Date dt = makeDate("11-June-07");

		DValue val = new DValueImpl(type, dt);
		assertEquals(type, val.getType());

		Date dt2 = (Date) val.getObject();
		assertSame(dt, dt2);
		assertEquals("Mon Jun 11 00:00:00 EDT 2007", dt2.toString());
	}
	
	private Date makeDate(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy") ; 
		Date dt = null;
		try {
			dt = sdf.parse(s);
			log(dt.toString());
		} catch (ParseException e) {
		}
		return dt;
	}

	@Test
	public void testBuilder() {
		DType type = registry.getType(BuiltInTypes.DATE_SHAPE);
		XDateValueBuilder builder = new XDateValueBuilder(type);

		builder.buildFromString("11-June-07");
		assertEquals(false, builder.wasSuccessful());
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		for(NewErrorMessage err: builder.getValidationErrors()) {
			log(err.getMessage());
		}

		DValue dval = builder.getDValue();
		Date dt = (Date) dval.getObject();
		assertEquals("Mon Jun 11 00:00:00 EDT 2007", dt.toString());
		assertTrue(dval.getType()== type);

		log("2..");
		builder = new XDateValueBuilder(type);
		builder.buildFromString("xyz");
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("3..");
		builder = new XDateValueBuilder(type);
		builder.buildFromString(null);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());

		chkErrors(builder, 1);
		dval = builder.getDValue();
		assertEquals(null, dval);

		log("4..");
		builder = new XDateValueBuilder(type);
		dt = makeDate("11-June-07");
		builder.buildFrom(dt);
		builder.finish();
		assertEquals(true, builder.wasSuccessful());

		chkErrors(builder, 0);
		dval = builder.getDValue();
		assertEquals("Mon Jun 11 00:00:00 EDT 2007", dval.asDate().toString());
	}
	
	@Test
	public void testStructBuilder() {
		DType eltype = registry.getType(BuiltInTypes.DATE_SHAPE);
        OrderedMap fieldMap = new OrderedMap();
		fieldMap.add("field1", eltype, false, false);
		fieldMap.add("field2", eltype, false, false);
		DStructType type = new DStructType(Shape.STRUCT, "xyz", null, fieldMap);
		registerType("xyz", type);

		XStructValueBuilder builder = new XStructValueBuilder(type);
		builder.addField("field1", buildDateVal(registry, "11-June-2007"));
		builder.addField("field2", buildDateVal(registry, "11-June-2007"));
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		Map<String,DValue> map = dval.asMap();
		assertEquals(2, map.size());
		
		log("2..");
		builder = new XStructValueBuilder(type);
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 2);
		
		log("3..");
		builder = new XStructValueBuilder(type);
		builder.addField("fieldx",  buildDateVal(registry, "11-June-2007"));
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);

		log("4..");
		builder = new XStructValueBuilder(type);
		builder.addField("", buildDateVal(registry, "11-June-2007"));
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);

		log("5..");
		builder = new XStructValueBuilder(type);
		builder.addField("field1", buildIntVal(registry, 444));
		builder.addField("field2",  buildDateVal(registry, "11-June-2007"));
		builder.finish();
		assertEquals(false, builder.wasSuccessful());
		chkErrors(builder, 1);
	}

	//-----
}
