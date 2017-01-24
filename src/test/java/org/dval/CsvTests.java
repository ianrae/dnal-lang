//package org.dval;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import org.dval.builder.StructValueBuilder;
//import org.dval.csv.CSVParser;
//import org.dval.fluent.type.TypeBuilder;
//import org.dval.nrule.NRuleContext;
//import org.dval.nrule.SimpleNRuleRunner;
//import org.dval.nrule.ValidationScorer;
//import org.junit.Test;
//
//public class CsvTests extends BaseDValTest {
//	
//	@Test
//	public void test() {
//		DStructType type = buildProductType(registry);
//		
//		String hdr = "code;desc;age";
//		String line = "1234;pb;25";
//		
//		String[] arHdr = hdr.split(";");
//		String[] ar = line.split(";");
//		
//		StructValueBuilder builder = new StructValueBuilder(world, type);
//		int index = 0;
//		for(String fieldName: arHdr) {
//			log(fieldName);
//			if (fieldName.equals("age")) {
//				builder.addField(fieldName, buildIntVal(registry, Integer.parseInt(ar[index])));
//			} else {
//				builder.addField(fieldName, buildStringVal(registry, ar[index]));
//			}
//			index++;
//		}
//		builder.finish();
//		chkErrors(builder, 0);
//		DValue person = builder.getDValue();
//		DStructHelper helper = person.asStruct();
//		assertEquals("1234", helper.getField("code").asString());
//		assertEquals("pb", helper.getField("desc").asString());
//		assertEquals(25L,  helper.getField("age").asLong());
//		
//		SimpleNRuleRunner runner = new SimpleNRuleRunner();
//		NRuleContext ctx = new NRuleContext();
//		runner.evaluate(person, ctx);
//		world.dump();
//	}
//	@Test
//	public void testParser() {
//		CSVParser parser = new CSVParser(registry);
//		String hdr = "code;desc;age";
//
//		parser.parseHdr(hdr, "Product", "age");
//		parser.parseLine("1234;pb;31");
//		parser.parseLine("1235;jam;32");
//		parser.parseLine("1236;bread;33");
//
//		DValue person = parser.getDvalList().get(0);
//		DStructHelper helper = person.asStruct();
//		assertEquals("1234", helper.getField("code").asString());
//		assertEquals("pb", helper.getField("desc").asString());
//		assertEquals(31L,  helper.getField("age").asLong());
//		
//		assertTrue(parser.validate());
//		world.dump();
//		ValidationScorer scorer = new ValidationScorer();
//		world.scoreWorld(scorer);
//		chkScorer(scorer, true, false, false);
//	}
//	@Test
//	public void testParser2() {
//		CSVParser parser = new CSVParser(registry);
//		DStructType type = buildProductType(registry);
//		String hdr = "code;desc;age";
//		
//		parser.setupType(type, hdr);
//		parser.parseLine("1234;pb;31");
//		parser.parseLine("1235;jam;32");
//		parser.parseLine("1236;bread;33");
//
//		DValue person = parser.getDvalList().get(0);
//		DStructHelper helper = person.asStruct();
//		assertEquals("1234", helper.getField("code").asString());
//		assertEquals("pb", helper.getField("desc").asString());
//		assertEquals(31L,  helper.getField("age").asLong());
//		
//		parser.validate();
//		dumpValErrors(parser.getValidationErrors());
//		assertFalse(parser.wasSuccessful());
//		world.dump();
//		ValidationScorer scorer = new ValidationScorer();
//		world.scoreWorld(scorer);
//		chkScorer(scorer, false, true, false);
//		registry.dump();
//	}
//	@Test
//	public void testParser3() {
//		CSVParser parser = new CSVParser(registry);
//		DStructType type = buildProductType(registry);
//		String hdr = "code;desc;age";
//		
//		parser.setupType(type, hdr);
//		parser.parseLine("123456;pb;31");
//		parser.parseLine("123566;jam;32");
//		parser.parseLine("123677;bread;33");
//
//		DValue person = parser.getDvalList().get(0);
//		DStructHelper helper = person.asStruct();
//		assertEquals("123456", helper.getField("code").asString());
//		assertEquals("pb", helper.getField("desc").asString());
//		assertEquals(31L,  helper.getField("age").asLong());
//		
//		NRuleContext.immediateLogErrors = true;
//		assertTrue(parser.validate());
//		world.dump();
//		ValidationScorer scorer = new ValidationScorer();
//		world.scoreWorld(scorer);
//		chkScorer(scorer, true, false, false);
//		registry.dump();
//	}
////	
////	@Test
////	public void testParserEnum() {
////		CSVParser parser = buildTitleEnum();
////
////		dumpValErrors(parser.getValidationErrors());
////		assertEquals(true, parser.wasSuccessful());
////		DValue person = parser.getDvalList().get(0);
////		DStructHelper helper = person.asStruct();
////		assertEquals("Mr", helper.getField("code").asString());
////		
////		assertTrue(parser.validate());
////		world.dump();
////		ValidationScorer scorer = new ValidationScorer();
////		world.scoreWorld(scorer);
////		chkScorer(scorer, true, false, false);
////		registry.dump();
////	}
////	
////	@Test
////	public void testParserEnum2() {
////		buildTitleEnum();
////		CSVParser parser = new CSVParser(registry);
////		DStructType type = buildEmployeeType(registry);
////		String hdr = "name;title";
////		
////		parser.setupType(type, hdr);
////		parser.parseLine("bob;Mr");
////		parser.parseLine("sue;Mrs");
////
////		dumpValErrors(parser.getValidationErrors());
////		assertEquals(true, parser.wasSuccessful());
////		DValue employee = parser.getDvalList().get(0);
////		DStructHelper helper = employee.asStruct();
////		assertEquals("bob", helper.getField("name").asString());
////		
////		DRef dref = (DRef) helper.getField("title");
////		DValue titleVal = (DValue) dref.getObject();
////		assertNotNull(titleVal); //resolved!
////		DStructHelper titleHelper = titleVal.asStruct();
////		assertEquals("Mr", titleHelper.getField("code").asString());
////		
////		assertTrue(parser.validate());
////		world.dump();
////		ValidationScorer scorer = new ValidationScorer();
////		world.scoreWorld(scorer);
////		chkScorer(scorer, true, false, false);
////		registry.dump();
////	}
//	
//	//----
//	private CSVParser buildTitleEnum() {
//		CSVParser parser = new CSVParser(registry);
//		DStructType type = buildTitleType(registry);
//		String hdr = "code;";
//		
//		parser.setupType(type, hdr);
//		parser.parseLine("Mr");
//		parser.parseLine("Mrs");
//
//		dumpValErrors(parser.getValidationErrors());
//		assertEquals(true, parser.wasSuccessful());
//		parser.validate();
//		assertEquals(true, parser.wasSuccessful());
//		return parser;
//	}
//	
//	private DStructType buildProductType(DTypeRegistry registry) {
//		TypeBuilder tb = new TypeBuilder(registry);
//		tb.start("Product")
//		.string("code").minSize(6)
//		.string("desc")
//		.integer("age")
//		.end();
//
//		DStructType type = tb.getType();
//		return type;
//	}
//	
//	//----
//	private DStructType buildTitleType(DTypeRegistry registry) {
//		TypeBuilder tb = new TypeBuilder(registry);
//		tb.start("Title")
//		.string("code")
//		.end();
//
//		DStructType type = tb.getType();
//		return type;
//	}
//	private DStructType buildEmployeeType(DTypeRegistry registry) {
//		DStructType refType = (DStructType) registry.getType("Title");
//		
//		TypeBuilder tb = new TypeBuilder(registry);
//		tb.start("Employee")
//		.string("name")
//		.reference("title", refType, "code")
//		.end();
//
//		DStructType type = tb.getType();
//		return type;
//	}
//
//}
