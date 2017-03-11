package org.dnal.core.nrule;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.ErrorType;
import org.dnal.core.NewErrorManager;
import org.dnal.core.NewErrorMessage;

public class NRuleContext {
//	public static boolean immediateLogErrors = false;
	
//	public List<NewErrorMessage> errL = new ArrayList<>();
	private XErrorTracker et;

	public NRuleContext(XErrorTracker et) {
		this.et = et;
	}
	public void addErrorZ(ErrorType errType, String message) {
		NewErrorMessage nem = NewErrorManager.OldErrorMsg(errType, message);
		nem.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
		addError(nem);
	}	
	public void addError(NewErrorMessage valerr ) {
//		NewErrorMessage nem = new NewErrorMessage();
//		nem.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
//		nem.setFieldName("?");
//		nem.setTypeName("?");
//		nem.setLineNum(valerr.getLineNum());
//		nem.setMessage(valerr.getMessage());
//		nem.setErrorName(valerr.getErrorType().name());
		
		valerr.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
		et.addError(valerr);
//		if (immediateLogErrors) {
//			System.out.println(String.format("fail: %s - %s", valerr.getErrorType().name(), valerr.getMessage()));
//		}
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
}