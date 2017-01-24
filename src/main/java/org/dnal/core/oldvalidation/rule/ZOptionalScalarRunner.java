package org.dnal.core.oldvalidation.rule;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;

public class ZOptionalScalarRunner extends ZRunner {

	public ZOptionalScalarRunner(List<ErrorMessage> valErrorList, String fnName) {
		super(valErrorList, fnName);
	}

	@Override
	public boolean willAccept(DValue dval) {
		if (dval.getType().isScalarShape()) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(DValue dval, String ruleText) {
		boolean notOptional = ruleText.startsWith("!");
		boolean b = (dval.getObject() == null);

		if (notOptional && b) {
			addRuleFailedError(ruleText);			
		}
	}

}