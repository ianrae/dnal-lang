package org.dval.oldvalidation.rule;

import java.util.List;

import org.dval.DValue;
import org.dval.Shape;
import org.dval.ErrorMessage;
import org.dval.oldvalidation.ExpresssionParser;

public abstract class ZStringRunner extends ZRunner {

	public ZStringRunner(List<ErrorMessage> valErrorList, String fnName) {
		super(valErrorList, fnName);
	}

	@Override
	public boolean willAccept(DValue dval) {
		return dval.getType().isShape(Shape.STRING);
	}

	@Override
	public void execute(DValue dval, String ruleText) {
		ExpresssionParser parser = new ExpresssionParser();
		String s = parser.parseFunctionArg(ruleText, getFnName());
		if (s == null) {
			addUnknownRuleError(ruleText);
		} else {
			boolean polarity = !ruleText.startsWith("!");
			executeStringExpression(dval, ruleText, s, polarity);
		}
	}

	protected abstract void executeStringExpression(DValue dval, String ruleText, String s, boolean polarity);
}