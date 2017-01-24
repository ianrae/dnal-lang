package com.github.ianrae.dnalparse.parser.ast;

import java.util.List;

public class FullListTypeExp extends FullTypeExp {
	public IdentExp elementType; //list<int>

	public FullListTypeExp(IdentExp varname, IdentExp typename, IdentExp elementType, List<RuleExp> ruleList) {
		super(varname, typename, ruleList);
		this.elementType = elementType;
	}
	public String strValue() {
		return super.strValue();
	}
	
	public String getListElementType() {
		String s = elementType.name();
		int pos = s.indexOf('<');
		int pos2 = s.indexOf('>');
		String elType = s.substring(pos + 1, pos2);
		return elType;
	}
}