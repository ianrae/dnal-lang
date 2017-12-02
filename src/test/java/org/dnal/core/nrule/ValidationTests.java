package org.dnal.core.nrule;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.TreeMap;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.BaseDValTest;
import org.dnal.core.BuiltInTypes;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.OrderedMap;
import org.dnal.core.Shape;
import org.dnal.core.nrule.CompareRule;
import org.dnal.core.nrule.NEmptyRule;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.SimpleNRuleRunner;
import org.dnal.core.nrule.virtual.VirtualInt;
import org.dnal.core.nrule.virtual.VirtualList;
import org.dnal.core.nrule.virtual.VirtualString;
import org.dnal.core.xbuilder.XListValueBuilder;
import org.dnal.core.xbuilder.XStructValueBuilder;
import org.junit.Test;

public class ValidationTests extends BaseDValTest {

	@Test
	public void test1() {
		DType type = registry.getType(BuiltInTypes.STRING_SHAPE);
		String s = "z";

		DValue dval = buildStringVal(registry, s);
		assertEquals(type, dval.getType());
		assertEquals("z", dval.asString());

		VirtualString vs = new VirtualString();
		NRule rule = new NEmptyRule<VirtualString>("empty", vs);
//		WrapperRule<VirtualString> wrapper = new WrapperRule<VirtualString>("wrap", rule, vs);
		type.getRawRules().add(rule);
		
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		NRuleContext ctx = createContext();
		runner.evaluate(dval, ctx);
		xchkValErrors(runner, 1);
		chkInvalid(dval);
		
		DValue dval2 = buildStringVal(registry, "");
		runner.evaluate(dval2, ctx);
		xchkValErrors(runner, 1);
		chkValid(dval2);
	}

	@Test
	public void testScalar() {
		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
		Integer n = 15;

		DValue dval = buildIntVal(registry, n);
		assertEquals(type, dval.getType());
		assertEquals(15, dval.asInt());

		VirtualInt vs = new VirtualInt();
		NRule rule = new CompareRule<VirtualInt, Integer>(">", ">", vs, 20);
//		WrapperRule<VirtualInt> wrapper = new WrapperRule<VirtualInt>("wrap", rule, vs);
		type.getRawRules().add(rule);
		
		
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		NRuleContext ctx = createContext();
		runner.evaluate(dval, ctx);
		xchkValErrors(runner, 1);
		chkInvalid(dval);
		
		DValue dval2 = buildIntVal(registry, 30);
		runner.evaluate(dval2, ctx);
		xchkValErrors(runner, 1);
		chkValid(dval2);
	}
	
	@Test
	public void testList() {
		DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
		DListType type = new DListType(Shape.LIST, "mylist", null, eltype);
		registerType("mylist", type);
		
		XListValueBuilder builder = new XListValueBuilder(type);
		DValue sval = buildStringVal(registry, "123");
		builder.addValue(sval);
		sval = buildStringVal(registry, "");
		builder.addValue(sval);
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		
		VirtualList vs = new VirtualList();
		
		NRule rule = new NEmptyRule<VirtualList>("empty", vs);
//		WrapperRule<VirtualList> wrapper = new WrapperRule<VirtualList>("wrap", rule, vs);
		type.getRawRules().add(rule);
		
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		NRuleContext ctx = createContext();
		runner.evaluate(dval, ctx);
		xchkValErrors(runner, 1);
		chkInvalid(dval);
	}
	@Test
	public void testStruct() {
		DType eltype = registry.getType(BuiltInTypes.STRING_SHAPE);
		OrderedMap fieldMap = new OrderedMap();
		fieldMap.add("field1", eltype, false, false);
		fieldMap.add("field2", eltype, false, false);
		DStructType type = new DStructType(Shape.STRUCT, "mylist", null, fieldMap);
		registerType("mylist", type);
		
		XStructValueBuilder builder = new XStructValueBuilder(type);
		builder.addField("field1", buildStringVal(registry, "123"));
		builder.addField("field2", buildStringVal(registry, ""));
		builder.finish();
		assertEquals(true, builder.wasSuccessful());
		DValue dval = builder.getDValue();
		Map<String,DValue> map = dval.asMap();
		assertEquals(2, map.size());
		
		VirtualString vs = new VirtualString();
		NRule rule = new NEmptyRule<VirtualString>("empty", vs);
//		WrapperRule<VirtualString> wrapper = new WrapperRule<VirtualString>("wrap", rule, vs);
		eltype.getRawRules().add(rule);
		
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		NRuleContext ctx = createContext();
		runner.evaluate(dval, ctx);
		xchkValErrors(runner, 1);
		chkInvalid(dval);
	}
	
	//--
	private NRuleContext createContext() {
		NRuleContext ctx = new NRuleContext(new XErrorTracker());
		return ctx;
	}

}
