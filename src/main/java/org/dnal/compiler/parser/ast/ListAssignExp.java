package org.dnal.compiler.parser.ast;

import java.util.List;

public class ListAssignExp extends ExpBase implements ValueExp {
	public List<Exp> list;

	public ListAssignExp(List<Exp> list) {
		this.list = list;
	}
	@Override
	public String strValue() {
		return "??";
	}
}