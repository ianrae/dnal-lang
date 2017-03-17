package org.dnal.compiler.nrule;

import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleBase;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualDataItem;

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
    	if (! ctx.getValidateOptions().isModeSet(validationMode)) {
    		return true; //don't execute
    	}
    	
        arg1.resolve(dval, ctx);
        boolean pass = onEval(dval, ctx);
        return applyPolarity(pass);
    }
    
    protected abstract boolean onEval(DValue dval, NRuleContext ctx);
    
    protected abstract String generateRuleText();

    @Override
    public String getRuleText() {
        if (super.getRuleText() != null) {
            return super.getRuleText();
        }
        String s = generateRuleText();
        setRuleText(s);
        return super.getRuleText();
    }

}