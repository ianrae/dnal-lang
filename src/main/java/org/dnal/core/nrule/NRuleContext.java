package org.dnal.core.nrule;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.ErrorType;
import org.dnal.core.NewErrorManager;
import org.dnal.core.NewErrorMessage;

public class NRuleContext {
//	public static boolean immediateLogErrors = false;
	
	public List<NewErrorMessage> errL = new ArrayList<>();
	private XErrorTracker et;

	public NRuleContext(XErrorTracker et) {
		this.et = et;
	}
	public void addErrorZ(ErrorType errType, String message) {
		NewErrorMessage nem = NewErrorManager.OldErrorMsg(errType, message);
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
		
		errL.add(valerr);
//		if (immediateLogErrors) {
//			System.out.println(String.format("fail: %s - %s", valerr.getErrorType().name(), valerr.getMessage()));
//		}
	}
	public boolean wereNoErrors() {
		return errL.size() == 0;
	}
	public int getErrorCount() {
		return errL.size();
	}
}