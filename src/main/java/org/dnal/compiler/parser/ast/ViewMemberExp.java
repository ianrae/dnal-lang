package org.dnal.compiler.parser.ast;

import org.codehaus.jparsec.Token;

public class ViewMemberExp implements Exp {
	
	public IdentExp left;
	public IdentExp right;
	public IdentExp rightType;
	public ViewDirection direction;

	public ViewMemberExp(IdentExp left, Token tok, IdentExp right, IdentExp type) {
		this.left = left;
		this.right = right;
		this.rightType = type;
		String tokStr = getTokString(tok);
		this.direction = (tokStr.equals("->")) ? ViewDirection.OUTBOUND : ViewDirection.INBOUND;
	}
	private String getTokString(Token tok) {
		if (tok == null) {
			return "";
		} else {
			return tok.toString();
		}
	}
	
	public String strValue() {
		return left.val;
	}
}