package com.github.ianrae.dnalparse.parser.ast;

public class ComparisonAndRuleExp extends RuleExp {
	public ComparisonRuleExp exp1;
	public ComparisonRuleExp exp2;

	public ComparisonAndRuleExp(ComparisonRuleExp exp1, ComparisonRuleExp exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}
	public String strValue() {
		return String.format("%s and %s", exp1.strValue(), exp2.strValue());
	}
}