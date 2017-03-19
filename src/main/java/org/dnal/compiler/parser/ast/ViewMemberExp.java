package org.dnal.compiler.parser.ast;

public class ViewMemberExp implements Exp {
	
	public IdentExp left;
	public IdentExp right;

	public ViewMemberExp(IdentExp left, IdentExp right) {
		this.left = left;
		this.right = right;
	}
	
	public String strValue() {
		return left.val;
	}
}