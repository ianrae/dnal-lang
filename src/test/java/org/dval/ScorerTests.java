package org.dval;

import org.dval.oldbuilder.XStructValueBuilder;

public class ScorerTests extends BaseDValTest {
	
//	@Test
//	public void testValMustExist() {
//		DType type = registry.getType(BuiltInTypes.REF_SHAPE);
//		DStructType addrType = buildAddressType(registry);
//		DStructType personType = buildPersonType(registry, type);
//
//		DValue addr = buildAddress(registry, addrType);
//		DRef dref = buildRef(registry, addr.getType(), "Person.address", "101");
//		DValue person = buildPerson(registry, personType, dref);
//		
//		assertEquals("code", dref.getFieldName());
//		assertNotNull(dref.getObject());
//		
//		NRule rule = new NReferenceExistsRule("refExists", dref);
//		dref.getType().getRawRules().add(rule);
//		
//		SimpleNRuleRunner runner = new SimpleNRuleRunner();
//		NRuleContext ctx = new NRuleContext();
//		runner.evaluate(person, ctx);
//		xchkValErrors(runner, 0);
//		
//		log("must not exist..");
//		dref.getType().getRawRules().clear();
//		NotRule notrule = new NotRule("!refExists", rule);
//		dref.getType().getRawRules().add(notrule);
//		
//		runner = new SimpleNRuleRunner();
//		runner.evaluate(person, ctx);
//		xchkValErrors(runner, 1);
//		chkInvalid(dref);
//		
//		runner.evaluate(addr, ctx);
//		
//		world.dump();
//		
//		ValidationScorer scorer = new ValidationScorer();
//		world.scoreWorld(scorer);
//		chkScorer(scorer, false, true, false);
//		chkScorerCounts(scorer, 5, 2, 0);
//	}
	
//	@Test
//	public void testNotResolve() {
//		DType type = registry.getType(BuiltInTypes.REF_SHAPE);
//		DStructType addrType = buildAddressType(registry);
//		DStructType personType = buildPersonType(registry, type);
//
//		DValue addr = buildAddress(registry, addrType);
//		DRef dref = buildRef(registry, addr.getType(), "Person.address", "102");
//		DValue person = buildPerson(registry, personType, dref);
//		
//		assertEquals("code", dref.getFieldName());
//		assertNull(dref.getObject());
//		log("a");
//		Map<String, DValue> map = person.asMap();
//		DValue dv2 = map.get("address");
//		DRef dr2 = (DRef) dv2;
//		DValue dv3 = (DValue) dr2.getObject();
//		assertNull(dv3);
//		
//		world.dump();
//		VRule rule = new VRule("referenceExists");
//		dref.getType().getRawRules().add(rule);
//		
//		SimpleNRuleRunner runner = new SimpleNRuleRunner();
//		NRuleContext ctx = new NRuleContext();
//		runner.evaluate(person, ctx);
//		xchkValErrors(runner, 1);
//		chkInvalid(dref);
//	}
//	
//	
	//-----
	private DStructType buildAddressType(DTypeRegistry registry) {
		DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
        OrderedMap fieldMap = new OrderedMap();
		fieldMap.add("code", eltype, false, false);
		fieldMap.add("field2", eltype, false, false);
		DStructType type = new DStructType(Shape.STRUCT, "Address", null, fieldMap);
		registerType("Address", type);
		return type;
	}
	private DStructType buildPersonType(DTypeRegistry registry, DType refType) {
		DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
        OrderedMap fieldMap = new OrderedMap();
		fieldMap.add("field1", eltype, false, false);
		fieldMap.add("field2", eltype, false, false);
		fieldMap.add("address", refType, false, false);
		DStructType type = new DStructType(Shape.STRUCT, "Person", null, fieldMap);
		registerType("Person", type);
		return type;
	}
	
	private DValue buildAddress(DTypeRegistry registry, DStructType addrType) {
		XStructValueBuilder builder = new XStructValueBuilder(addrType);
		builder.addField("code", buildStringVal(registry, "101"));
		builder.addField("field2", buildStringVal(registry, "abc"));
		builder.finish();
		chkErrors(builder, 0);
		DValue addr = builder.getDValue();
		return addr;
	}
	private DValue buildPerson(DTypeRegistry registry, DStructType personType, DValue dref) {
		XStructValueBuilder builder = new XStructValueBuilder(personType);
		builder.addField("field1", buildStringVal(registry, "bom"));
		builder.addField("field2", buildStringVal(registry, "smith"));
		builder.addField("address", dref);
		builder.finish();
		chkErrors(builder, 0);
		DValue person = builder.getDValue();
		return person;
	}
	
}
