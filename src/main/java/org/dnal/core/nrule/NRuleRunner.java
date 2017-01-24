package org.dnal.core.nrule;

import org.dnal.core.DValue;

public interface NRuleRunner {
//	public NRuleContext ctx = new NRuleContext();
	boolean run(DValue dval, NRule rule, NRuleContext ctx);
}