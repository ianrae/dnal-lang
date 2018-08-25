package org.dnal.compiler.parser.ast;

public class IdentExp extends ExpBase {
	public String val;

	public IdentExp(String s) {
		this.val =s;
	}
	
	public String name() {
		return val;
	}

	@Override
	public String strValue() {
		return val;
	}
}