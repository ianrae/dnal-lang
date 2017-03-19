package org.dnal.compiler.parser.ast;

import java.util.List;

import org.codehaus.jparsec.Token;

public class ViewExp implements Exp {
	public enum Direction {
		INBOUND,
		OUTBOUND
	}
	
	public IdentExp viewName;
	public IdentExp typeName;
	public Direction direction;
	public List<ViewMemberExp> memberL;

	public ViewExp(IdentExp varname, Token tok, IdentExp typename) {
		this.viewName = varname;
		String tokStr = getTokString(tok);
		this.direction = (tokStr.equals("->")) ? Direction.OUTBOUND : Direction.INBOUND;
		this.typeName = typename;
	}
	public ViewExp(List<ViewMemberExp> arg0) {
		this.memberL = arg0;
	}
	public ViewExp(ViewExp src, ViewExp src2) {
		this.viewName = src.viewName;
		this.direction = src.direction;
		this.typeName = src.typeName;
		this.memberL = src2.memberL;
	}
	private String getTokString(Token tok) {
		if (tok == null) {
			return "";
		} else {
			return tok.toString();
		}
	}
	
	public String strValue() {
		return typeName.val;
	}
}