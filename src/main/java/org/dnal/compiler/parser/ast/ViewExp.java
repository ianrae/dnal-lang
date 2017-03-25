package org.dnal.compiler.parser.ast;

import java.util.List;

import org.codehaus.jparsec.Token;

public class ViewExp implements Exp {
	public IdentExp viewName;
	public IdentExp typeName;
	public ViewDirection direction;
	public List<ViewMemberExp> memberL;
	public boolean isOutputView;

	public ViewExp(IdentExp typename, Token tok, IdentExp varname) {
		this.viewName = varname;
		String tokStr = getTokString(tok);
		this.direction = (tokStr.equals("->")) ? ViewDirection.OUTBOUND : ViewDirection.INBOUND;
		this.typeName = typename;
	}
	public ViewExp(List<ViewMemberExp> arg0) {
		this.memberL = arg0;
	}
	public ViewExp(boolean isOutputView, ViewExp src, ViewExp src2) {
		this.viewName = src.viewName;
		this.direction = src.direction;
		this.typeName = src.typeName;
		this.memberL = src2.memberL;
		this.isOutputView = isOutputView;
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