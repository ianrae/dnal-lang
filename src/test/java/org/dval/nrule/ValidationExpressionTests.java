//package org.dval.validation;
//
//import org.dval.BaseDValTest;
//import org.dval.DTypeRegistry;
//import org.dval.DValue;
//import org.dval.validation.SimpleVRuleRunner;
//import org.dval.validation.VRule;
//import org.junit.Before;
//import org.junit.Test;
//
//public class ValidationExpressionTests extends BaseDValTest {
//
//	@Test
//	public void testScalarIntFail() {
//		DValue dval = buildIntVal(registry, 4);
//		runRuleFail(registry, dval, "min(100)");
//	}
//	@Test
//	public void testScalarIntOK() {
//		DValue dval = buildIntVal(registry, 100);
//		runRuleOK(registry, dval, "min(100)");
//	}
//	@Test
//	public void testScalarIntMaxFail() {
//		DValue dval = buildIntVal(registry, 4);
//		runRuleFail(registry, dval, "max(1)");
//	}
//	
//	@Test
//	public void testScalarIntMaxOK() {
//		DValue dval = buildIntVal(registry, 4);
//		runRuleOK(registry, dval, "max(10)");
//	}
//	
//	//---------
//	private void runRuleOK(DTypeRegistry registry, DValue dval, String ruleText) {
//		VRule rule = new VRule(ruleText);
//		dval.getType().getRawRules().add(rule);
//		
//		SimpleVRuleRunner runner = new SimpleVRuleRunner();
//		RuleContext ctx = new RuleContext();
//		runner.evaluate(dval, ctx);
//		chkValErrors(runner, 0);
//		chkValid(dval);
//	}
//	
//	private void runRuleFail(DTypeRegistry registry, DValue dval, String ruleText) {
//		VRule rule = new VRule(ruleText);
//		dval.getType().getRawRules().add(rule);
//		
//		SimpleVRuleRunner runner = new SimpleVRuleRunner();
//		RuleContext ctx = new RuleContext();
//		runner.evaluate(dval, ctx);
//		chkValErrors(runner, 1);
//		chkInvalid(dval);
//	}
//	
//	
//
//}
