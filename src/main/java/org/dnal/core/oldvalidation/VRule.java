package org.dnal.core.oldvalidation;

public class VRule {
	private String rule;
	public Object fixMeLater;

	public VRule(String rule) {
		super();
		this.rule = rule.trim();
	}

	public String getRule() {
		return rule;
	}
}