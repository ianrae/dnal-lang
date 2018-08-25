package org.dnal.compiler.parser.ast;

public class EnumMemberExp extends ExpBase {
	public IdentExp var;
	public IdentExp type;

	public EnumMemberExp(IdentExp varname, IdentExp type) {
		this.var = varname;
		this.type = type;
	}
	@Override
	public String strValue() {
		return var.val;
	}
}