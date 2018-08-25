package org.dnal.compiler.parser.ast;

public class IdentExp extends ExpBase {
	public String val;

	public IdentExp(int pos, String s) {
		this.pos = pos;
		this.val =s;
	}
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