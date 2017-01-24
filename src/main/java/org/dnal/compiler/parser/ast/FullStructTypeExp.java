package org.dnal.compiler.parser.ast;

import java.util.List;

public class FullStructTypeExp extends FullTypeExp {
	public StructExp members;

	public FullStructTypeExp(IdentExp varname, IdentExp typename, StructExp members, List<RuleExp> ruleList) {
		super(varname, typename, ruleList);
		this.members = members;
	}
	public String strValue() {
		return super.strValue();
	}
}