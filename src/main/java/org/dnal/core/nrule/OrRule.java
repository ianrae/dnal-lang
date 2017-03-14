package org.dnal.core.nrule;

import org.dnal.core.DValue;

public class OrRule extends NRuleBase {
	public static final String NAME = "or";
	
	private NRule rule1;
	private NRule rule2;
	
	public OrRule(String name, NRule rule1, NRule rule2) {
		super(name);
		this.rule1 = rule1;
		this.rule2 = rule2;
	}
	
	@Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
		if (rule1.eval(dval, ctx)) {
			return true;
		}
		return rule2.eval(dval, ctx);
	}
}