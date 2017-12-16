package org.dnal.compiler.parser.ast;

public class FullAssignmentExp implements Exp {
	public IdentExp var;
	public IdentExp type;
	public Exp value;

	public FullAssignmentExp(IdentExp varname, IdentExp typename, Exp val) {
		this.var = varname;
		this.type = typename;
		this.value = val;
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
	public boolean isMapVar() {
        if (type.val.startsWith("map<")) {
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
	public IdentExp getMapSubType() {
        String target = "map<";
        String s = type.val.substring(target.length());
        if (s.endsWith(">")) {
            return new IdentExp(s.substring(0, s.length() - 1));
        } else {
//            addError2s("var%s: malformed type '%s.", "", type.val);
            return null;
        }
    }   	
}