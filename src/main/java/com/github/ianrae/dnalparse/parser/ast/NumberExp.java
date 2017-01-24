package com.github.ianrae.dnalparse.parser.ast;

public class NumberExp implements ValueExp {
    public Double val;

    public NumberExp(Double s) {
        this.val = s;
    }
    public String strValue() {
        return val.toString();
    }
}