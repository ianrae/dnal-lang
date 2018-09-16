package org.dnal.compiler.parser.ast;

import java.util.List;

public class StructExp extends ExpBase implements ValueExp {
	public List<StructMemberExp> list;

	public StructExp(List<StructMemberExp> list) {
		this.list = list;
	}
	@Override
	public String strValue() {
		return "??";
	}
}