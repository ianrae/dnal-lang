package org.dnal.compiler.parser.ast;

public class StringExp implements ValueExp {
	public String val;

	public StringExp(String s) {
		this.val = s;
	}
	public String strValue() {
		return val.toString();
	}
}