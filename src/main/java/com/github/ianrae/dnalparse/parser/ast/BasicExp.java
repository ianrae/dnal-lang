package com.github.ianrae.dnalparse.parser.ast;

public class BasicExp implements Exp {
	public String val;

	public BasicExp(String s) {
		this.val =s;
	}

	public String strValue() {
		return val;
	}
}