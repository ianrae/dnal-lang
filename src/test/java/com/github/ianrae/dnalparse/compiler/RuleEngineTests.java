package com.github.ianrae.dnalparse.compiler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dval.DValue;
import org.dval.DValueImpl;
import org.dval.ErrorMessage;
import org.dval.nrule.AndRule;
import org.dval.nrule.CompareRule;
import org.dval.nrule.EqRule;
import org.dval.nrule.NRule;
import org.dval.nrule.NRuleBase;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.NotRule;
import org.dval.nrule.OrRule;
import org.dval.nrule.StaticRule;
import org.dval.nrule.virtual.VirtualString;
import org.junit.Test;

import com.github.ianrae.dnalparse.nrule.Custom1Rule;

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
			for(ErrorMessage msg: runner.ctx.errL) {
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
