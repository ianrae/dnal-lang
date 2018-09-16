package org.dnal.compiler.parser.ast;

import java.util.List;

public class EnumExp extends ExpBase implements ValueExp {
	public List<EnumMemberExp> list;

	public EnumExp(List<EnumMemberExp> list) {
		this.list = list;
	}
	@Override
	public String strValue() {
		return "??";
	}
}