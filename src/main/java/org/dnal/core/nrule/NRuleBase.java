package org.dnal.core.nrule;

import org.dnal.core.DValue;
import org.dnal.core.ErrorType;
import org.dnal.core.nrule.virtual.VirtualDataItem;

public abstract class NRuleBase implements NRule {
	private String name;
	private String ruleText; //for logging errors
	public boolean polarity = true;
	
	public NRuleBase(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
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
        boolean pass = onEval(dval, ctx);
        return applyPolarity(pass);
    }
    
    protected abstract boolean onEval(DValue dval, NRuleContext ctx);
    
    protected void resolveArg(Object arg, DValue dval, NRuleContext ctx) {
        if (arg instanceof VirtualDataItem) {
            VirtualDataItem vs = (VirtualDataItem) arg;
            vs.resolve(dval, ctx);
        }
    }
    
    protected void setActualValue(Object val1, NRuleContext ctx) {
		if (val1 != null) {
			ctx.setActualValue(val1.toString());
		}
	}

	protected void addUnknownRuleError(NRuleContext ctx, String ruleText) {
		ctx.addError(ErrorType.UNKNOWNRULE, 
				String.format("uknown rule '%s'", ruleText));
	}
	protected void addUnknownFieldError(NRuleContext ctx, String ruleText, String fieldName) {
		ctx.addError(ErrorType.UNKNOWNRULE, 
				String.format("uknown field '%s' in rule '%s'", fieldName, ruleText));
	}
	protected void addInvalidRuleError(NRuleContext ctx, String ruleText) {
		ctx.addError(ErrorType.INVALIDRULE, 
				String.format("invalid rule can't be used here '%s'", ruleText));
	}
	protected void addRuleFailedError(NRuleContext ctx, String ruleText) {
		ctx.addError(ErrorType.RULEFAIL, ruleText + "- failed");
	}

    public String getRuleText() {
        return ruleText;
    }

    public void setRuleText(String ruleText) {
        this.ruleText = ruleText;
    }
	
}