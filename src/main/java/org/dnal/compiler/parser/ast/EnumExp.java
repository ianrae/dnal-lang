package org.dnal.compiler.parser.ast;

import java.util.List;

public class EnumExp implements ValueExp {
	public List<EnumMemberExp> list;

	public EnumExp(List<EnumMemberExp> list) {
		this.list = list;
	}
	public String strValue() {
		return "??";
	}
}