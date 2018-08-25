package org.dnal.compiler.parser.ast;

import org.codehaus.jparsec.Token;

public class StructMemberExp implements Exp {
	public int pos;
	public IdentExp var;
	public IdentExp type;
	public boolean optional;
	public boolean isUnique;

	public StructMemberExp(int pos, IdentExp varname, IdentExp type, Token opt, Token unique) {
		this.pos = pos;
		this.var = varname;
		this.type = type;
		this.optional = (opt != null);
		this.isUnique = (unique != null);
	}
	public String strValue() {
		return var.val;
	}
	
    public boolean isListVar() {
        if (type.val.startsWith("list<")) {
            return true;
        }
        return false;
    }   
    public IdentExp getListSubType() {
        String target = "list<";
        String s = type.val.substring(target.length());
        if (s.endsWith(">")) {
            return new IdentExp(s.substring(0, s.length() - 1));
        } else {
//            addError2s("var%s: malformed type '%s.", "", type.val);
            return null;
        }
    }       
	
}