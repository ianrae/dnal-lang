//package org.dval.validation;
//
//import static org.junit.Assert.assertEquals;
//
//import org.dval.BaseDValTest;
//import org.dval.BuiltInTypes;
//import org.dval.DType;
//import org.dval.DValue;
//import org.junit.Test;
//
//public class CompositeRuleTests extends BaseDValTest {
//
//	@Test
//	public void test1() {
//		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
//		DValue dval = buildIntVal(registry, 100);
//
//		SimpleVRuleRunner runner = new SimpleVRuleRunner();
//		VRule rule = new VRule("min(50)");
//		type.getRawRules().add(rule);
//		RuleContext ctx = new RuleContext();
//		runner.evaluate(dval, ctx);
//		chkValErrors(runner, 0);
//		chkValid(dval);
//	}
//
//	@Test
//	public void testOr() {
//		String op = "or";
//		chkCompositeRule(op, "min(50)", "max(500)", true, 0);
//		chkCompositeRule(op, "min(150)", "max(500)", true, 1);
//	}
//	@Test
//	public void testOrFail() {
//		String op = "or";
//		chkCompositeRule(op, "min(150)", "max(50)", false, 2);
//	}
//	
//	@Test
//	public void testAnd() {
//		String op = "and";
//		chkCompositeRule(op, "min(50)", "max(500)", true, 0);
//	}
//	@Test
//	public void testAndFail() {
//		String op = "and";
//		chkCompositeRule(op, "min(150)", "max(500)", false, 1);
//	}
//	@Test
//	public void testAndFail2() {
//		String op = "and";
//		chkCompositeRule(op, "max(500)", "min(150)", false, 1);
//	}
//	
//	private void chkCompositeRule(String op, String leftRule, String rightRule, boolean ok, int failCount) {
//		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
//		DValue dval = buildIntVal(registry, 100);
//
//		SimpleVRuleRunner runner = new SimpleVRuleRunner();
//		VRule rule1 = new VRule(leftRule);
//		VRule rule2 = new VRule(rightRule);
//		CompositeRule crule = new CompositeRule(op, rule1, rule2);
//		type.getRawRules().add(crule);
//		RuleContext ctx = new RuleContext();
//		runner.evaluate(dval, ctx);
//		if (ok){
//			chkValErrors(runner, failCount);
//			chkValid(dval);
//		} else {
//			chkValErrors(runner, failCount);
//			chkInvalid(dval);
//		}
//	}
//}