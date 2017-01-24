package org.dnal.core.oldvalidation.rule;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;

public class ZEqIntRunner extends ZIntegerRunner {
	public ZEqIntRunner(List<ErrorMessage> valErrorList, String fnName) {
		super(valErrorList, fnName);
	}

	@Override
	protected void executeIntegerExpression(DValue dval, String ruleText, Long targetVal) {
		boolean eq = !ruleText.startsWith("!");
		long lval = dval.asLong();
		
		boolean pass = false;
		if (eq) {
			pass = (lval == targetVal);
		} else {
			pass = (lval != targetVal);
		}
		
		if (! pass) {
			addRuleFailedError(ruleText);			
		}
	}
}