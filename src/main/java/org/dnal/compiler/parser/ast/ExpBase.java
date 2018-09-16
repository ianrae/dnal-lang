package org.dnal.compiler.parser.ast;

public abstract class ExpBase implements Exp {
	public int pos = 0;

	public int getPos() {
		return pos;
	}
}
