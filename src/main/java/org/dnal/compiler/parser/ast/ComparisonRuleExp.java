package org.dnal.compiler.parser.ast;

public class ComparisonRuleExp extends RuleExp {
    public Exp optionalArg;  //may be null
	public String op;
	public Integer val;
	public Long longVal;
	public Double zval;
	public String strVal;
	public String identVal; //for enum and reference
	
	public ComparisonRuleExp(Exp optArg, String op, Exp valExp) {
	    this.optionalArg = optArg;
		this.op = op;
		if (valExp instanceof IntegerExp) {
			IntegerExp iexp = (IntegerExp) valExp;
			this.val = iexp.val;
		} else if (valExp instanceof LongExp) {
			LongExp iexp = (LongExp) valExp;
			this.longVal = iexp.val;
		} else if (valExp instanceof NumberExp) {
			NumberExp iexp = (NumberExp) valExp;
			this.zval = iexp.val;
		} else if (valExp instanceof StringExp) {
			StringExp iexp = (StringExp) valExp;
			this.strVal = iexp.val;
		} else if (valExp instanceof IdentExp) {
			IdentExp iexp = (IdentExp) valExp;
			this.identVal = iexp.val;
		} else {
			throw new IllegalArgumentException("uknown ComparisonRulExp arg: " + valExp.getClass().getSimpleName());
		}
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
        } else if (someval instanceof IdentExp) {
            this.identVal = ((IdentExp)someval).val;
        } else {
            throw new IllegalArgumentException("unsupported Exp type");
        }
    }
	@Override
	public String strValue() {
	    String s = null;
	    if (val != null) {
	        s = String.format("%d", val);
	    } else if (zval != null) {
	        s = String.format("%g", zval);
	    } else if (strVal != null) {
	        s = strVal;
        } else if (longVal != null) {
            s = String.format("%d", longVal);
        } else if (identVal != null) {
            s = identVal;
        }
	    
		return String.format("%s %s", op, s);
	}
}