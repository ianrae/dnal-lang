package com.github.ianrae.dnalparse.parser.ast;

import java.util.List;

public class StructAssignExp implements Exp {
	public List<Exp> list;

	public StructAssignExp(List<Exp> list) {
		this.list = list;
	}
	public String strValue() {
		return "??";
	}
}