package org.dnal.compiler.parser.ast;

public class LongExp extends ExpBase implements ValueExp {
    public Long val;

    public LongExp(int pos, Long s) {
    	this.pos = pos;
        this.val =s;
    }
    public LongExp(Long s) {
        this.val =s;
    }
    @Override
    public String strValue() {
        return val.toString();
    }
}