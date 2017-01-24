package com.github.ianrae.dnalparse.parser.ast;

public class LongExp implements ValueExp {
    public Long val;

    public LongExp(Long s) {
        this.val =s;
    }
    public String strValue() {
        return val.toString();
    }
}