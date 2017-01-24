package org.dval.nrule;

import org.dval.DValue;

public interface NRuleRunner {
//	public NRuleContext ctx = new NRuleContext();
	boolean run(DValue dval, NRule rule, NRuleContext ctx);
}