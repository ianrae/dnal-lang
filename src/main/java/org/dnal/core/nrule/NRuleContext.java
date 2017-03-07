package org.dnal.core.nrule;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.ErrorMessage;
import org.dnal.core.NewErrorMessage;

public class NRuleContext {
	public static boolean immediateLogErrors = false;
	
	public List<NewErrorMessage> errL = new ArrayList<>();
	
	public void addError(ErrorMessage valerr ) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
		nem.setFieldName("?");
		nem.setTypeName("?");
		nem.setLineNum(valerr.getLineNum());
		nem.setMessage(valerr.getMessage());
		nem.setErrorName(valerr.getErrorType().name());
		
		errL.add(nem);
		if (immediateLogErrors) {
			System.out.println(String.format("fail: %s - %s", valerr.getErrorType().name(), valerr.getMessage()));
		}
	}
	public boolean wereNoErrors() {
		return errL.size() == 0;
	}
	public int getErrorCount() {
		return errL.size();
	}
}