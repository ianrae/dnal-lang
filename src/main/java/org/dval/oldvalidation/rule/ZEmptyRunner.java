package org.dval.oldvalidation.rule;

import java.util.List;

import org.dval.DValue;
import org.dval.ErrorMessage;

public class ZEmptyRunner extends ZStringRunner {
	public ZEmptyRunner(List<ErrorMessage> valErrorList, String fnName) {
		super(valErrorList, fnName);
	}

	@Override
	protected void executeStringExpression(DValue dval, String ruleText, String s, boolean polarity) {
		boolean b = dval.asString().isEmpty();
		if (b != polarity) {
			addRuleFailedError(ruleText);			}
	}
}