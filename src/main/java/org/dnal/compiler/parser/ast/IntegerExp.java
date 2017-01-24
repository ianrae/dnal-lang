package org.dnal.compiler.parser.ast;

public class IntegerExp implements ValueExp {
	public Integer val;

	public IntegerExp(Integer s) {
		this.val =s;
	}
	public String strValue() {
		return val.toString();
	}
}