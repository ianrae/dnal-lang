package org.dnal.compiler.parser.ast;

import java.util.List;

public class StructAssignExp extends ExpBase {
	public List<Exp> list;

	public StructAssignExp(List<Exp> list) {
		this.list = list;
	}
	@Override
	public String strValue() {
		return "??";
	}
}