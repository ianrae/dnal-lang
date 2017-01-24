package com.github.ianrae.dnalparse.parser.ast;

public class ComparisonRuleExp extends RuleExp {
    public IdentExp optionalArg;  //may be null
	public String op;
	public Integer val;
	public Long longVal;
	public Double zval;
	public String strVal;
	
	public ComparisonRuleExp(IdentExp optArg, String op, Integer val) {
	    this.optionalArg = optArg;
		this.op = op;
		this.val = val;
	}
    public ComparisonRuleExp(IdentExp optArg, String op, Double zval) {
        this.optionalArg = optArg;
        this.op = op;
        this.zval = zval;
    }
    public ComparisonRuleExp(IdentExp optArg, String op, Exp someval) {
        this.optionalArg = optArg;
        this.op = op;
        if (someval instanceof IntegerExp) {
            this.val = ((IntegerExp)someval).val;
        } else if (someval instanceof LongExp) {
            this.longVal = ((LongExp)someval).val;
        } else if (someval instanceof NumberExp) {
            this.zval = ((NumberExp)someval).val;
        } else if (someval instanceof StringExp) {
            this.strVal = ((StringExp)someval).val;
        } else {
            throw new IllegalArgumentException("unsupported Exp type");
        }
    }
	public String strValue() {
	    String s = null;
	    if (val != null) {
	        s = String.format("%d", val);
	    } else if (zval != null) {
	        s = String.format("%d", zval);
	    } else if (strVal != null) {
	        s = strVal;
        } else if (longVal != null) {
            s = String.format("%d", longVal);
        }
	    
		return String.format("%s %s", op, s);
	}
}