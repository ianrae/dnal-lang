package org.dnal.core.oldvalidation.rule;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.Shape;
import org.dnal.core.oldvalidation.ExpresssionParser;

public abstract class ZIntegerRunner extends ZRunner {

	public ZIntegerRunner(List<ErrorMessage> valErrorList, String fnName) {
		super(valErrorList, fnName);
	}

	@Override
	public boolean willAccept(DValue dval) {
		return dval.getType().isShape(Shape.INTEGER);
	}

	@Override
	public void execute(DValue dval, String ruleText) {
		ExpresssionParser parser = new ExpresssionParser();
		Long min = parser.parseFunctionArgLong(ruleText, getFnName());
		if (min == null) {
			addUnknownRuleError(ruleText);
		} else {
			executeIntegerExpression(dval, ruleText, min);
		}
	}

	protected abstract void executeIntegerExpression(DValue dval, String ruleText, Long lval);
}