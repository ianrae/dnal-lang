package org.dnal.core;
//package org.dval;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import org.dval.csv.CSVParser;
//import org.dval.fluent.type.TypeBuilder;
//import org.dval.nrule.ValidationScorer;
//import org.junit.Test;
//
//public class CsvDateTests extends BaseDValTest {
//	
//	@Test
//	public void testParser() {
//		CSVParser parser = new CSVParser(registry);
//		parser.setDateFormat("yyyy-MMM-dd");
//		//todo! SimpleDateFormat often accepts wrong format input
//		DStructType type = buildOrderType(registry);
//		String hdr = "code;date;completed";
//		
//		parser.setupType(type, hdr);
//		parser.parseLine("123456;2007-June-11;true");
//		parser.parseLine("123566;2007-June-11;false");
//		parser.parseLine("123677;2007-June-11;false");
//		dumpValErrors(parser.getValidationErrors());
//		assertEquals(true, parser.wasSuccessful());
//
//		DValue person = parser.getDvalList().get(0);
//		DStructHelper helper = person.asStruct();
//		assertEquals("123456", helper.getField("code").asString());
//		assertEquals("Mon Jun 11 00:00:00 EDT 2007", helper.getField("date").asDate().toString());
//		assertEquals(true, helper.getField("completed").asBoolean());
//		
//		assertTrue(parser.validate());
//		world.dump();
//		ValidationScorer scorer = new ValidationScorer();
//		world.scoreWorld(scorer);
//		chkScorer(scorer, true, false, false);
//		registry.dump();
//	}
//	
//	//----
//	private DStructType buildOrderType(DTypeRegistry registry) {
//		TypeBuilder tb = new TypeBuilder(registry);
//		tb.start("Product")
//		.string("code").minSize(6)
//		.date("date")
//		.bool("completed")
//		.end();
//
//		DStructType type = tb.getType();
//		return type;
//	}
//	
//
//}
