package org.dval.nrule;

import org.dval.DValue;

public class StaticRule extends NRuleBase {
	public StaticRule(String name, boolean b) {
		super(name);
		this.b = b;
	}

	public boolean b;

	@Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
		return b;
	}
}