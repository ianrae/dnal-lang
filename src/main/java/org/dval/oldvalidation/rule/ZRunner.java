package org.dval.oldvalidation.rule;

import java.util.List;

import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.ErrorType;

public abstract class ZRunner {
	protected List<ErrorMessage> valErrorList;
	private String fnName;

	public ZRunner(List<ErrorMessage> valErrorList2, String fnName) {
		this.fnName = fnName;
		this.valErrorList = valErrorList2;
	}

	public abstract boolean willAccept(DValue dval);
	public abstract void execute(DValue dval, String ruleText);

	protected void addUnknownRuleError(String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.UNKNOWNRULE, 
				String.format("uknown rule '%s'", ruleText));
		this.valErrorList.add(err);
	}
	protected void addInvalidRuleError(String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.INVALIDRULE, 
				String.format("invalid rule can't be used here '%s'", ruleText));
		this.valErrorList.add(err);
	}
	protected void addRuleFailedError(String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.RULEFAIL, ruleText + "- failed");
		this.valErrorList.add(err);
	}

	public List<ErrorMessage> getValidationErrors() {
		return valErrorList;
	}

	public String getFnName() {
		return fnName;
	}
}