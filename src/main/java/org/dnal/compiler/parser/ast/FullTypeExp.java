package org.dnal.compiler.parser.ast;

import java.util.List;

public class FullTypeExp implements Exp {
	public IdentExp var;
	public IdentExp type;
	public List<RuleExp> ruleList;

	public FullTypeExp(IdentExp varname, IdentExp typename, List<RuleExp> ruleList) {
		this.var = varname;
		this.type = typename;
		this.ruleList = ruleList;
	}
	public String strValue() {
		return var.val;
	}
}