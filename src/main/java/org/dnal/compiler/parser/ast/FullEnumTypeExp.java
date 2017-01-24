package org.dnal.compiler.parser.ast;

import java.util.List;

public class FullEnumTypeExp extends FullTypeExp {
	public EnumExp members;

	public FullEnumTypeExp(IdentExp varname, IdentExp typename, EnumExp members, List<RuleExp> ruleList) {
		super(varname, typename, ruleList);
		this.members = members;
	}
	public String strValue() {
		return super.strValue();
	}
}