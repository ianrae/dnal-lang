package org.dnal.compiler.parser.ast;

public class IntegerExp extends ExpBase implements ValueExp {
	public Integer val;

	public IntegerExp(int pos, Integer s) {
		this.pos = pos;
		this.val =s;
	}
	public IntegerExp(Integer s) {
		this.val =s;
	}
	@Override
	public String strValue() {
		return val.toString();
	}
}