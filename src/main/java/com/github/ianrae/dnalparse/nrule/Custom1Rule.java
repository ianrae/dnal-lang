package com.github.ianrae.dnalparse.nrule;

import org.dval.DValue;
import org.dval.nrule.NRuleBase;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.virtual.VirtualDataItem;

public abstract class Custom1Rule<T extends VirtualDataItem> extends NRuleBase {
	public T arg1;
	
	public Custom1Rule(String name, T arg1) {
		super(name);
		this.arg1 = arg1;
	}
	
	protected boolean applyPolarity(boolean pass){
	    if (polarity) {
	        return pass;
	    } else {
	        return !pass;
	    }
	}

    @Override
    public boolean eval(DValue dval, NRuleContext ctx) {
        arg1.resolve(dval, ctx);
        boolean pass = onEval(dval, ctx);
        return applyPolarity(pass);
    }
    
    protected abstract boolean onEval(DValue dval, NRuleContext ctx);
}