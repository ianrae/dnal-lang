package org.dnal.compiler.parser.ast;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jparsec.Token;
import org.dnal.core.util.StringTrail;

public class ViewMemberExp implements Exp {
	
	public IdentExp left;
	public List<IdentExp> leftList = new ArrayList<>();
	public IdentExp right;
	public IdentExp rightType;
	public ViewDirection direction;

	public ViewMemberExp(List<List<IdentExp>> leftL, Token tok, IdentExp right, IdentExp type) {

        for(List<IdentExp> sublist : leftL) {
        	for(IdentExp exp: sublist) {
        		if (left == null) {
        			this.left = exp;
        		} else {
        			leftList.add(exp);
        		}
        	}
        }
		
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
	
	public String getFullLeft() {
		StringTrail trail = new StringTrail();
		trail.setDelim(".");
		trail.add(left.val);
		for(IdentExp exp: leftList) {
			trail.add(exp.val);
		}
		return trail.toString();
	}
	
	public String strValue() {
		return left.val;
	}
}