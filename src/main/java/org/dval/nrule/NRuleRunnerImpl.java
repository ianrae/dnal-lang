package org.dval.nrule;

import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.ErrorType;

public class NRuleRunnerImpl implements NRuleRunner {
    public static int ruleCounter; //for testing only
    
	@Override
	public boolean run(DValue dval, NRule rule, NRuleContext ctx) {
		ruleCounter++;
		int initial = ctx.getErrorCount();
		boolean b = rule.eval(dval, ctx);
		if (!b && (ctx.getErrorCount() == initial)) {
		    String s = String.format("%s: %s", rule.getName(), rule.getRuleText());
			ErrorMessage err = new ErrorMessage(ErrorType.RULEFAIL, s);
			ctx.addError(err);
		}
		return b;
	}

}
