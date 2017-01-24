package org.dval.nrule;

import org.dval.DValue;

public interface NRule {
	boolean eval(DValue dval, NRuleContext ctx);
	String getName();
	String getRuleText();
	void setRuleText(String ruleText);
}