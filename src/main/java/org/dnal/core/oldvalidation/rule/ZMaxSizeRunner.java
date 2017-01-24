package org.dnal.core.oldvalidation.rule;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;

public class ZMaxSizeRunner extends ZStringRunner {
	public ZMaxSizeRunner(List<ErrorMessage> valErrorList, String fnName) {
		super(valErrorList, fnName);
	}

	@Override
	protected void executeStringExpression(DValue dval, String ruleText, String s, boolean polarity) {
		Long max = Long.parseLong(s);
		if (max == null) {
			addUnknownRuleError(ruleText);
		} else {
			String ds = dval.asString();
			if (ds.length() > max) {
				this.addRuleFailedError(ruleText);
			}
		}
	}
}