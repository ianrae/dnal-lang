package org.dnal.core.nrule;

import org.dnal.core.DValue;

public class NotRule extends NRuleBase {
	private NRule rule1;
	
	public NotRule(String name, NRule rule1) {
		super(name);
		this.rule1 = rule1;
	}
	
	@Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
		return ! rule1.eval(dval, ctx);
	}
}