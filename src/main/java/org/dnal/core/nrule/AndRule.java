package org.dnal.core.nrule;

import org.dnal.core.DValue;

public class AndRule extends NRuleBase {
	private NRule rule1;
	private NRule rule2;
	
	public AndRule(String name, NRule rule1, NRule rule2) {
		super(name);
		this.rule1 = rule1;
		this.rule2 = rule2;
	}
	
	@Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
		if (! rule1.eval(dval, ctx)) {
			return false;
		}
		return rule2.eval(dval, ctx);
	}
}