package org.dnal.compiler.parser.ast;

import java.util.List;

public class FullTypeExp extends ExpBase {
	public IdentExp var;
	public IdentExp type;
	public List<RuleExp> ruleList;

	public FullTypeExp(int pos, IdentExp varname, IdentExp typename, List<RuleExp> ruleList) {
		this.pos = pos;
		this.var = varname;
		this.type = typename;
		this.ruleList = ruleList;
	}
	@Override
	public String strValue() {
		return var.val;
	}
}
