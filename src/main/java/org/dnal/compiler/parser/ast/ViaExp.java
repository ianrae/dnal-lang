package org.dnal.compiler.parser.ast;


public class ViaExp extends ExpBase {
    
    public IdentExp typeExp;
    public IdentExp fieldExp;
    public Exp valueExp;
    public ViaExp extraViaExp; //set during ast-dval generation

    public ViaExp(int pos, String typeName, String fieldName, Exp value) {
    	this.pos = pos;
        this.typeExp = new IdentExp(typeName);
        this.fieldExp = new IdentExp(fieldName);
        this.valueExp = value;
    }
    public ViaExp(int pos, IdentExp exp, IdentExp field, Exp value) {
    	this.pos = pos;
        this.typeExp = exp;
        this.fieldExp = field;
        this.valueExp = value;
    }

    @Override
    public String strValue() {
        return fieldExp.name();
    }

}
