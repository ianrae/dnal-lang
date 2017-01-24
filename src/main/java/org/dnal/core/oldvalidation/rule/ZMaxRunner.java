package org.dnal.core.oldvalidation.rule;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;

public class ZMaxRunner extends ZIntegerRunner {
	public ZMaxRunner(List<ErrorMessage> valErrorList, String fnName) {
		super(valErrorList, fnName);
	}

	@Override
	protected void executeIntegerExpression(DValue dval, String ruleText, Long targetVal) {
		long lval = dval.asLong();
		if (lval > targetVal) {
			addRuleFailedError(ruleText);			
		}
	}
}