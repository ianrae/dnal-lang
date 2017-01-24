package org.dnal.compiler.parser.ast;

public class IdentExp implements Exp {
	public String val;

	public IdentExp(String s) {
		this.val =s;
	}
	
	public String name() {
		return val;
	}

	public String strValue() {
		return val;
	}
}