package org.dnal.compiler.parser.ast;

import java.util.List;

public class FullEnumTypeExp extends FullTypeExp {
	public EnumExp members;

	public FullEnumTypeExp(int pos, IdentExp varname, IdentExp typename, EnumExp members, List<RuleExp> ruleList) {
		super(pos, varname, typename, ruleList);
		this.members = members;
	}
	@Override
	public String strValue() {
		return super.strValue();
	}
}