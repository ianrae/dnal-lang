package org.dnal.compiler.parser.ast;

public class BasicExp extends ExpBase {
	public String val;
	public BasicExp(String s) {
		this.val =s;
	}

	@Override
	public String strValue() {
		return val;
	}
}