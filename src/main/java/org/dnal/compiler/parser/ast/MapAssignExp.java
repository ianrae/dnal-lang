package org.dnal.compiler.parser.ast;

import java.util.List;

public class MapAssignExp extends ExpBase implements ValueExp {
	public List<Exp> list;

	public MapAssignExp(List<Exp> list) {
		this.list = list;
	}
	@Override
	public String strValue() {
		return "??";
	}
}