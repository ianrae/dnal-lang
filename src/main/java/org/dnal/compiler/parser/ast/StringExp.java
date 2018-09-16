package org.dnal.compiler.parser.ast;

public class StringExp extends ExpBase implements ValueExp {
	public String val;

	public StringExp(String s) {
		this.val = s;
	}
	@Override
	public String strValue() {
		return val.toString();
	}
}