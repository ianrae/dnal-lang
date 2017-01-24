package org.dnal.core.oldvalidation.rule;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;

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