package org.dval.oldvalidation.rule;

import java.util.List;

import org.dval.DValue;
import org.dval.ErrorMessage;

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