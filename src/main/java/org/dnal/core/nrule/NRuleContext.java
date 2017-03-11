package org.dnal.core.nrule;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.ErrorType;
import org.dnal.core.NewErrorMessage;

public class NRuleContext {
	private XErrorTracker et;

	public NRuleContext(XErrorTracker et) {
		this.et = et;
	}
	public void addError(ErrorType errType, String message) {
        NewErrorMessage nem = new NewErrorMessage();
        nem.setErrorName(errType.name());
        nem.setMessage(message);
//        nem.setSrcFile("?");
		nem.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
		addError(nem);
	}	
	public void addError(NewErrorMessage valerr ) {
		valerr.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
		et.addError(valerr);
	}
	public boolean wereNoErrors() {
		return et.areErrors() == false;
	}
	public int getErrorCount() {
		return et.getErrorCount();
	}
	public List<NewErrorMessage> getErrors() {
		return et.getErrL();
	}
	public void setCurrentTypeName(String currentTypeName) {
		et.setCurrentTypeName(currentTypeName);
	}
	public void setCurrentFieldName(String currentFieldName) {
		et.setCurrentFieldName(currentFieldName);
	}
	public void setCurrentVarName(String currentVarName) {
		et.setCurrentVarName(currentVarName);
	}
	public void setActualValue(String currentActualValue) {
		et.setCurrentActualValue(currentActualValue);
	}
}