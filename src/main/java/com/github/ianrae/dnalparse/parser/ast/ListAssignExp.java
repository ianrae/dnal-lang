package com.github.ianrae.dnalparse.parser.ast;

import java.util.List;

public class ListAssignExp implements ValueExp {
	public List<Exp> list;

	public ListAssignExp(List<Exp> list) {
		this.list = list;
	}
	public String strValue() {
		return "??";
	}
}