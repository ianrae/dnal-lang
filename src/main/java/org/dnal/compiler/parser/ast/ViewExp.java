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
	public List<RuleExp> ruleList;

	public ViewExp(IdentExp varname, Token tok, IdentExp typename) {
		this.viewName = varname;
		String tokStr = getTokString(tok);
		this.direction = (tokStr.equals("->")) ? Direction.OUTBOUND : Direction.INBOUND;
		this.typeName = typename;
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