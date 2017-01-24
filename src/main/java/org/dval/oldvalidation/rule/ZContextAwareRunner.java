package org.dval.oldvalidation.rule;

import java.util.List;

import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldvalidation.RuleContext;

public abstract class ZContextAwareRunner extends ZRunner {

	public ZContextAwareRunner(List<ErrorMessage> valErrorList2, String fnName) {
		super(valErrorList2, fnName);
	}

	public abstract boolean willAccept(DValue dval);
	
	@Override
	public void execute(DValue dval, String ruleText) {
		throw new RuntimeException("ZContextAwareRunner wrong method");
	}
	public abstract void execute(DValue dval, String ruleText, RuleContext ctx);

}