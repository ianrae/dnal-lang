package com.github.ianrae.dnalparse.compiler;

import static org.junit.Assert.assertEquals;

import org.dnal.compiler.nrule.Custom1Rule;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.nrule.AndRule;
import org.dnal.core.nrule.CompareRule;
import org.dnal.core.nrule.EqRule;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.NotRule;
import org.dnal.core.nrule.OrRule;
import org.dnal.core.nrule.StaticRule;
import org.dnal.core.nrule.virtual.VirtualString;
import org.junit.Test;

public class RuleEngineTests {
	
	
	public static class MyRule1 extends Custom1Rule<VirtualString> { 
		
		public MyRule1(String name, VirtualString arg1) {
			super(name, arg1);
		}

		@Override
	    protected boolean onEval(DValue dval, NRuleContext ctx) {
			return arg1.val.contains("a");
		}
	}
	
	
	public static class NRuleRunner {
		public NRuleContext ctx = new NRuleContext();
		public boolean run(DValue dval, NRule rule) {
			return rule.eval(dval, ctx);
		}
	}

	public interface VirtualDataItem {
	}
	
	public static class VirtualInt implements VirtualDataItem, Comparable<Integer> {
		public Integer val;

		@Override
		public int compareTo(Integer arg0) {
			return val.compareTo(arg0);
		}
	}
	
	@Test
	public void test() {
		StaticRule rule = new StaticRule("A", true);
		NRuleRunner runner = createRunner();
		boolean b = runner.run(null, rule);
		assertEquals(true, b);

		rule.b = false;
		b = runner.run(null, rule);
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
	
	@Test
	public void testCustom1() {
		VirtualString vs1 = new VirtualString();
        DValue dval = new DValueImpl(null, "bac");
		chkRule(new MyRule1("rule1", vs1), dval, true);
        dval = new DValueImpl(null, "");
		chkRule(new MyRule1("rule1", vs1), dval, false);
	}
	
	@Test
	public void testVirtualInt() {
		VirtualInt vi = new VirtualInt();
		vi.val = 10;

		Integer n2 = 5;
		NRule rule1 = new CompareRule<VirtualInt, Integer>("A", ">", vi, n2);
		chkRule(rule1,  true);
		vi.val = 0;
		chkRule(rule1,  false);
	}
	
	//--
    private void chkRule(NRule rule, boolean expected) {
        chkRule(rule, null, expected);
    }
	private void chkRule(NRule rule, DValue dval, boolean expected) {
		NRuleRunner runner = createRunner();
		boolean b = runner.run(dval, rule);
		
		if (! runner.ctx.wereNoErrors()) {
			for(NewErrorMessage msg: runner.ctx.errL) {
				log(msg.getMessage());
			}
		}
		
		
		assertEquals(true, runner.ctx.wereNoErrors());
		assertEquals(expected, b);
	}
	
	private NRuleRunner createRunner() {
		NRuleRunner runner = new NRuleRunner();
		return runner;
	}
	
	private void log(String s) {
		System.out.println(s);
	}

}
