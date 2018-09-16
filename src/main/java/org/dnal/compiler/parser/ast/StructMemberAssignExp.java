package org.dnal.compiler.parser.ast;

public class StructMemberAssignExp extends ExpBase {
	public IdentExp var;
	public Exp value;

	public StructMemberAssignExp(int pos, IdentExp varname, Exp value) {
		this.pos = pos;
		this.var = varname;
		this.value = value;
	}
	@Override
	public String strValue() {
		return var.val;
	}
}