package org.dnal.core.nrule;

import org.dnal.core.DValue;

public interface NRule {
	boolean eval(DValue dval, NRuleContext ctx);
	String getName();
	String getRuleText();
	void setRuleText(String ruleText);
	int getMode();
}