package com.github.ianrae.dnalparse.parser.ast;

public class EnumMemberExp implements Exp {
	public IdentExp var;
	public IdentExp type;

	public EnumMemberExp(IdentExp varname, IdentExp type) {
		this.var = varname;
		this.type = type;
	}
	public String strValue() {
		return var.val;
	}
}