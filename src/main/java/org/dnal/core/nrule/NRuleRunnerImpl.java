package org.dnal.core.nrule;

import org.dnal.core.DValue;
import org.dnal.core.ErrorType;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.nrule.virtual.StructMember;

public class NRuleRunnerImpl implements NRuleRunner {
    public static int ruleCounter; //for testing only
    
	@Override
	public boolean run(DValue dval, NRule rule, NRuleContext ctx) {
		ruleCounter++;
		int initial = ctx.getErrorCount();
		boolean b = rule.eval(dval, ctx);
		if (!b && (ctx.getErrorCount() == initial)) {
		    String s = String.format("%s: %s", rule.getName(), rule.getRuleText());
	        NewErrorMessage nem = new NewErrorMessage();
	        nem.setErrorName(ErrorType.RULEFAIL.name());
	        nem.setMessage(s);
        	nem.setFieldName(rule.getFieldName()); //may be null
			ctx.addError(nem);
		    
		}
		return b;
	}

}
