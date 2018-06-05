package org.dnal.compiler.parser.ast;

import java.util.List;

public class MapAssignExp implements ValueExp {
	public List<Exp> list;

	public MapAssignExp(List<Exp> list) {
		this.list = list;
	}
	public String strValue() {
		return "??";
	}
}