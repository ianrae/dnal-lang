package org.dval.nrule;

import static org.junit.Assert.assertEquals;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.nrule.AndRule;
import org.dnal.core.nrule.CompareRule;
import org.dnal.core.nrule.EqRule;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.NRuleRunner;
import org.dnal.core.nrule.NotRule;
import org.dnal.core.nrule.OrRule;
import org.dnal.core.nrule.StaticRule;
import org.junit.Test;

public class NRuleTests {
	
	@Test
	public void test() {
		StaticRule rule = new StaticRule("A", true);
		NRuleRunner runner = createRunner();
		NRuleContext ctx = new NRuleContext();
		boolean b = runner.run(null, rule, ctx);
		assertEquals(true, b);

		rule.b = false;
		b = runner.run(null, rule, ctx);
		assertEquals(false, b);
	}
	
	@Test
	public void testEq() {
		Integer n1 = 4;
		Integer n2 = 5;
		chkRule(new EqRule<Integer, Integer>("A", "==", n1, n2), false);
		chkRule(new EqRule<Integer, Integer>("A", "!=", n1, n2), true);
	}
	@Test
	public void testEqFail() {
		Integer n1 = 4;
		Integer n2 = 5;
		chkRule(new EqRule<Integer, Integer>("A", "zz", n1, n2), false, false);
	}
	
	@Test
	public void testCompare() {
		Integer n1 = 4;
		Integer n2 = 5;
		Integer n3 = 4;
		chkRule(new CompareRule<Integer, Integer>("A", ">", n1, n2), false);
		chkRule(new CompareRule<Integer, Integer>("A", ">=", n1, n3), true);
		chkRule(new CompareRule<Integer, Integer>("A", "<", n1, n2), true);
		chkRule(new CompareRule<Integer, Integer>("A", "<=", n1, n3), true);
		chkRule(new CompareRule<Integer, Integer>("A", "<=", n2, n1), false);
	}
    @Test
    public void testCompareLong() {
        Long n1 = 4L;
        Long n2 = 5L;
        Long n3 = 4L;
        chkRule(new CompareRule<Long, Long>("A", ">", n1, n2), false);
        chkRule(new CompareRule<Long, Long>("A", ">=", n1, n3), true);
        chkRule(new CompareRule<Long, Long>("A", "<", n1, n2), true);
        chkRule(new CompareRule<Long, Long>("A", "<=", n1, n3), true);
        chkRule(new CompareRule<Long, Long>("A", "<=", n2, n1), false);
    }
	
	@Test
	public void testOr() {
		Integer n1 = 4;
		Integer n2 = 5;
		NRule rule1 = new CompareRule<Integer, Integer>("A", ">", n1, n2);
		NRule rule2 = new CompareRule<Integer, Integer>("A", ">=", n1, n2);
		OrRule orRule = new OrRule("A", rule1, rule2);
		chkRule(orRule, false);
		
		rule1 = new CompareRule<Integer, Integer>("A", ">", n2, n1);
		orRule = new OrRule("A", rule1, rule2);
		chkRule(orRule, true);
		chkRule(new NotRule("!A", orRule), false);
	}
	@Test
	public void testAnd() {
		Integer n1 = 4;
		Integer n2 = 5;
		NRule rule1 = new CompareRule<Integer, Integer>("A", ">", n1, n2);
		NRule rule2 = new CompareRule<Integer, Integer>("A", ">=", n1, n2);
		AndRule orRule = new AndRule("A", rule1, rule2);
		chkRule(orRule, false);
		
		rule1 = new CompareRule<Integer, Integer>("A", ">", n2, n1);
		rule2 = new CompareRule<Integer, Integer>("A", ">=", n2, n1);
		orRule = new AndRule("A", rule1, rule2);
		chkRule(orRule, true);
	}
	
	
	//--
	private void chkRule(NRule rule, boolean expected) {
		chkRule(rule, expected, true);
	}
	private void chkRule(NRule rule, boolean expected, boolean noErrors) {
		NRuleRunner runner = createRunner();
		NRuleContext ctx = new NRuleContext();
		boolean b = runner.run(null, rule, ctx);
		
		if (! ctx.wereNoErrors()) {
			for(ErrorMessage msg: ctx.errL) {
				log(msg.getMessage());
			}
		}
		
		
		assertEquals(noErrors, ctx.wereNoErrors());
		assertEquals(expected, b);
	}
	
	private static class MyRunner implements NRuleRunner {

		@Override
		public boolean run(DValue dval, NRule rule, NRuleContext ctx) {
			return rule.eval(dval, ctx);
		}
		
	}
	
	private NRuleRunner createRunner() {
		NRuleRunner runner = new MyRunner();
		return runner;
	}
	
	private void log(String s) {
		System.out.println(s);
	}

}
