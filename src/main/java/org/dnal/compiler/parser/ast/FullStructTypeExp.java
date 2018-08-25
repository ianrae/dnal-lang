package org.dnal.compiler.parser.ast;

import java.util.List;

public class FullStructTypeExp extends FullTypeExp {
	public StructExp members;

	public FullStructTypeExp(int pos, IdentExp varname, IdentExp typename, StructExp members, List<RuleExp> ruleList) {
		super(pos, varname, typename, ruleList);
		this.members = members;
	}
	@Override
	public String strValue() {
		return super.strValue();
	}
}