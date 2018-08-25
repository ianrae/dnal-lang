package org.dnal.compiler.parser.ast;

public class BooleanExp extends ExpBase implements ValueExp {
	public Boolean val;

	public BooleanExp(boolean b) {
		this.val = b;
	}
	@Override
	public String strValue() {
		return val.toString();
	}
}