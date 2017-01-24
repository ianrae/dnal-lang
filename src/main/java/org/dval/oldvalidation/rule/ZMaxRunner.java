package org.dval.oldvalidation.rule;

import java.util.List;

import org.dval.DValue;
import org.dval.ErrorMessage;

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