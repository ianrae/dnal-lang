package org.dnal.core.oldvalidation.rule;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;

public class ZMinSizeRunner extends ZStringRunner {
	public ZMinSizeRunner(List<ErrorMessage> valErrorList, String fnName) {
		super(valErrorList, fnName);
	}

	@Override
	protected void executeStringExpression(DValue dval, String ruleText, String s, boolean polarity) {
		Long min = Long.parseLong(s);
		if (min == null) {
			addUnknownRuleError(ruleText);
		} else {
			String ds = dval.asString();
			if (ds.length() < min) {
				this.addRuleFailedError(ruleText);
			}
		}
	}
}