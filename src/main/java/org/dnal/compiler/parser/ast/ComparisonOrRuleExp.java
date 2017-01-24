package org.dnal.compiler.parser.ast;

public class ComparisonOrRuleExp extends RuleExp {
	public ComparisonRuleExp exp1;
	public ComparisonRuleExp exp2;

	public ComparisonOrRuleExp(ComparisonRuleExp exp1, ComparisonRuleExp exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}
	public String strValue() {
		return String.format("%s or %s", exp1.strValue(), exp2.strValue());
	}
}