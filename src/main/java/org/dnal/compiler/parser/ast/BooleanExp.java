package org.dnal.compiler.parser.ast;

public class BooleanExp implements ValueExp {
	public Boolean val;

	public BooleanExp(boolean b) {
		this.val = b;
	}
	public String strValue() {
		return val.toString();
	}
}