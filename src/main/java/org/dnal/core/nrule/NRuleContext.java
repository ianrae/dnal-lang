package org.dnal.core.nrule;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.ErrorMessage;

public class NRuleContext {
	public static boolean immediateLogErrors = false;
	
	public List<ErrorMessage> errL = new ArrayList<>();
	
	public void addError(ErrorMessage valerr ) {
		errL.add(valerr);
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