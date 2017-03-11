package org.dnal.api.bean;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;

public class ValidationResult {
	
	private DValue dval;
	private List<NewErrorMessage> errors;

	public ValidationResult(DValue dval, List<NewErrorMessage> errors) {
		this.dval = dval;
		this.errors = errors;
	}

	public DValue getDval() {
		return dval;
	}

	public List<NewErrorMessage> getErrors() {
		return errors;
	}
	
	public boolean succeeded() {
		return errors.isEmpty();
	}

}
