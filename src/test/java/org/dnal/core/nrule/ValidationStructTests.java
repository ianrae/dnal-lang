package org.dnal.core.nrule;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.BaseDValTest;
import org.dnal.core.DStructType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.PersonHelper;
import org.dnal.core.fluent.type.TypeBuilder;
import org.dnal.core.nrule.NEmptyRule;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.SimpleNRuleRunner;
import org.dnal.core.nrule.virtual.VirtualString;
import org.dnal.core.nrule.virtual.VirtualStringMember;
import org.dnal.core.oldbuilder.XStructValueBuilder;
import org.junit.Before;
import org.junit.Test;

public class ValidationStructTests extends BaseDValTest {

	@Test
	public void test4() {
		DStructType type = personHelper.buildAddressType(registry);
		DValue dval = personHelper.buildAddress(registry, type);
		
		VirtualStringMember vs = new VirtualStringMember();
		vs.fieldName = "code";
		NRule rule = new NEmptyRule<VirtualString>("empty", vs);
//		WrapperRule<VirtualString> wrapper = new WrapperRule<VirtualString>("wrap1", rule, vs);
		
//		StructWrapperRule swr = new StructWrapperRule("swr", wrapper, "code");
		type.getRawRules().add(rule);
			
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		NRuleContext ctx = createContext();
		runner.evaluate(dval, ctx);
		xchkValErrors(runner, 1);
		chkInvalid(dval);
	}
	
	@Test
	public void testCity() {
		DStructType type = buildCityType(registry);
		DValue dval = buildCity(registry, type, "abc");
		
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		NRuleContext ctx = createContext();
		runner.evaluate(dval, ctx);
		xchkValErrors(runner, 0);
		chkValid(dval);
		registry.dump();
	}
	@Test
	public void testCityFail() {
		DStructType type = buildCityType(registry);
		DValue dval = buildCity(registry, type, "a");
		
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		NRuleContext ctx = createContext();
		runner.evaluate(dval, ctx);
		xchkValErrors(runner, 1);
		chkInvalid(dval);
	}

	//---------
	PersonHelper personHelper;
	
	@Before
	public void init() {
		super.init();
		personHelper = new PersonHelper(registry, world);
	}
	
	private NRuleContext createContext() {
		NRuleContext ctx = new NRuleContext(new XErrorTracker());
		return ctx;
	}
	
	//-----
	private DStructType buildCityType(DTypeRegistry registry) {
		TypeBuilder tb = new TypeBuilder(registry, world);
		tb.start("City")
		.string("name").minSize(2)
		.bool("flag2")
		.end();

		DStructType type = tb.getType();
		return type;
	}
	
	public DValue buildCity(DTypeRegistry registry, DStructType cityType, String cityName) {
		XStructValueBuilder builder = new XStructValueBuilder(cityType);
		builder.addField("name", buildStringVal(registry, cityName));
		builder.addField("flag2", buildBooleanVal(registry, false));
		builder.finish();
		chkErrors(builder, 0);
		DValue addr = builder.getDValue();
		return addr;
	}
	
}
