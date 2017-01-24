package org.dnal.core.nrule;

import org.dnal.core.DValue;


public class CompareRule<T extends Comparable, U extends Comparable> extends NRuleBase {
	public String op;
	public T val1;
	public U val2;
	
	public CompareRule(String name, String op, T val1, U val2) {
		super(name);
		this.op = op;
		this.val1 = val1;
		this.val2 = val2;
	}
	@Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
	    resolveArg(val1, dval, ctx);
	    resolveArg(val2, dval, ctx);
	    
		boolean b = false;
		switch(op) {
		case ">":
			b = (val1.compareTo(val2) > 0);
			break;
		case ">=":
			b = (val1.compareTo(val2) >= 0);
			break;
		case "<":
			b = (val1.compareTo(val2) < 0);
			break;
		case "<=":
			b = (val1.compareTo(val2) <= 0);
			break;
		default:
			this.addInvalidRuleError(ctx, String.format("CompareRule: unknown op '%s'", op));
			break;
		}
		return b;
	}
}