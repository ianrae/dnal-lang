package com.github.ianrae.dnalparse.parser.ast;

public class StructMemberAssignExp implements Exp {
	public IdentExp var;
	public Exp value;

	public StructMemberAssignExp(IdentExp varname, Exp value) {
		this.var = varname;
		this.value = value;
	}
	public String strValue() {
		return var.val;
	}
}