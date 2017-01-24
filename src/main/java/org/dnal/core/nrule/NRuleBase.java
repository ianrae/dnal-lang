package org.dnal.core.nrule;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
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

	protected void addUnknownRuleError(NRuleContext ctx, String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.UNKNOWNRULE, 
				String.format("uknown rule '%s'", ruleText));
		ctx.addError(err);
	}
	protected void addUnknownFieldError(NRuleContext ctx, String ruleText, String fieldName) {
		ErrorMessage err = new ErrorMessage(ErrorType.UNKNOWNRULE, 
				String.format("uknown field '%s' in rule '%s'", fieldName, ruleText));
		ctx.addError(err);
	}
	protected void addInvalidRuleError(NRuleContext ctx, String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.INVALIDRULE, 
				String.format("invalid rule can't be used here '%s'", ruleText));
		ctx.addError(err);
	}
	protected void addRuleFailedError(NRuleContext ctx, String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.RULEFAIL, ruleText + "- failed");
		ctx.addError(err);
	}

    public String getRuleText() {
        return ruleText;
    }

    public void setRuleText(String ruleText) {
        this.ruleText = ruleText;
    }
	
}