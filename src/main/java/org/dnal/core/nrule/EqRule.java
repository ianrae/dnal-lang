package org.dnal.core.nrule;

import org.dnal.core.DValue;

public class EqRule<T,U> extends NRuleBase {
	public static final String NAME = "equals";
	
	public String op;
	public T val1;
	public U val2;
	
	public EqRule(String name, String op, T val1, U val2) {
		super(name);
		this.op = op;
		this.val1 = val1;
		this.val2 = val2;
	}
	@Override
	protected boolean onEval(DValue dval, NRuleContext ctx) {
	    //val1 or val2 may be null due to optional
	    if (! containsValue(val1, dval) || ! containsValue(val2, dval)) {
	    	return true;
	    }
	    resolveArg(val1, dval, ctx);
	    resolveArg(val2, dval, ctx);
	    
	    setActualValue(val1, ctx);
	    
		boolean b = false;
		switch(op) {
		case "==":
			b = val1.equals(val2);
			break;
		case "!=":
			b = ! val1.equals(val2);
			break;
		default:
			this.addInvalidRuleError(ctx, String.format("eqRule: unknown op '%s'", op));
			break;
		}
		return b;
	}
}