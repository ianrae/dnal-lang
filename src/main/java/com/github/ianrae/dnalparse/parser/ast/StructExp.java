package com.github.ianrae.dnalparse.parser.ast;

import java.util.List;

public class StructExp implements ValueExp {
	public List<StructMemberExp> list;

	public StructExp(List<StructMemberExp> list) {
		this.list = list;
	}
	public String strValue() {
		return "??";
	}
}