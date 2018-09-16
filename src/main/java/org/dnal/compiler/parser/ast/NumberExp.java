package org.dnal.compiler.parser.ast;

public class NumberExp extends ExpBase implements ValueExp {
    public Double val;

    public NumberExp(int pos, Double s) {
    	this.pos = pos;
        this.val = s;
    }
    public NumberExp(Double s) {
        this.val = s;
    }
    @Override
    public String strValue() {
        return val.toString();
    }
}