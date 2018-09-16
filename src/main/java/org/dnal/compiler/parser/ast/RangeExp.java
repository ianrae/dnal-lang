package org.dnal.compiler.parser.ast;

public class RangeExp extends ExpBase implements ValueExp {
	public Integer from;
	public Integer to;
	
//	public RangeExp(IntegerExp from, IntegerExp to) {
//		this.from = from.val;
//		this.to = to.val;
//	}
    public RangeExp(int pos, Exp from, Exp to) {
    	this.pos = pos;
        if (from instanceof IntegerExp) {
            IntegerExp iexp = (IntegerExp) from;
            this.from = iexp.val;
        } else if (from instanceof NumberExp) {
            NumberExp nexp = (NumberExp) from;
            this.from = nexp.val.intValue(); //later check it has no fractional part!!
        } else {
            throw new IllegalArgumentException(String.format("RangeExp: wrong type"));
        }
        
        if (to instanceof IntegerExp) {
            IntegerExp iexp = (IntegerExp) to;
            this.to = iexp.val;
        } else if (to instanceof NumberExp) {
            NumberExp nexp = (NumberExp) to;
            this.to = nexp.val.intValue(); //later check it has no fractional part!!
        } else {
            throw new IllegalArgumentException(String.format("RangeExp: wrong type"));
        }
    }
    @Override
	public String strValue() {
		return String.format("%d..%d", from, to);
	}
}