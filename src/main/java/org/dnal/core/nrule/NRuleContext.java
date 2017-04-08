package org.dnal.core.nrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.validate.ValidationOptions;
import org.dnal.core.DValue;
import org.dnal.core.DValueProxy;
import org.dnal.core.ErrorType;
import org.dnal.core.NewErrorMessage;

public class NRuleContext {
	private XErrorTracker et;
	private Map<NRule,Integer> alreadyRunMap = new HashMap<>();
	private ValidationOptions validateOptions;
	private List<DValue> futureValues;

	public NRuleContext(XErrorTracker et) {
		this.et = et;
		this.validateOptions = new ValidationOptions();
		this.futureValues = new ArrayList<>();
	}
	public NRuleContext(XErrorTracker et, Map<NRule,Integer> alreadyRunMap, ValidationOptions validateOptions, List<DValue> futureValues) {
		this.et = et;
		this.validateOptions = validateOptions;
		this.alreadyRunMap = alreadyRunMap;
		this.futureValues = futureValues;
	}
	public void addError(ErrorType errType, String message) {
        NewErrorMessage nem = new NewErrorMessage();
        nem.setErrorName(errType.name());
        nem.setMessage(message);
		addError(nem);
	}	
	public void addErrorWithField(ErrorType errType, String message, String fieldName) {
        NewErrorMessage nem = new NewErrorMessage();
        nem.setErrorName(errType.name());
        nem.setMessage(message);
        nem.setFieldName(fieldName);
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
	
	public boolean haveAlreadyRun(NRule rule) {
		return alreadyRunMap.containsKey(rule);
	}
	public void addToAlreadyRunMap(NRule rule) {
		alreadyRunMap.put(rule, Integer.valueOf(0));
	}
	public ValidationOptions getValidateOptions() {
		return validateOptions;
	}
	public void addFutureValue(DValueProxy dval) {
		futureValues.add(dval);
	}
}