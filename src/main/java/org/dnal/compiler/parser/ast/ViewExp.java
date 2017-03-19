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
	private ViewMemberExp member;

	public ViewExp(IdentExp varname, Token tok, IdentExp typename) {
		this.viewName = varname;
		String tokStr = getTokString(tok);
		this.direction = (tokStr.equals("->")) ? Direction.OUTBOUND : Direction.INBOUND;
		this.typeName = typename;
	}
	public ViewExp(ViewExp src, ViewMemberExp member) {
		this.viewName = src.viewName;
		this.direction = src.direction;
		this.typeName = src.typeName;
		this.member = member;
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