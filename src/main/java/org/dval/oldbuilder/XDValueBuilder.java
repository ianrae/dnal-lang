package org.dval.oldbuilder;

import java.util.ArrayList;
import java.util.List;

import org.dval.DType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.ErrorType;

public abstract class XDValueBuilder {
	protected List<ErrorMessage> valErrorList = new ArrayList<>();
	protected boolean finished;
	protected DValue newDVal;
	protected DType type;
	
	public abstract void buildFromString(String input);

	public boolean finish() {
		finished = true;
		onFinish();
		boolean ok = wasSuccessful();
		return ok;
	}
	
	protected abstract void onFinish();

	public boolean wasSuccessful() {
		return finished && valErrorList.isEmpty();
	}
	public List<ErrorMessage> getValidationErrors() {
		return valErrorList;
	}
	public DValue getDValue() {
		return newDVal;
	}
	
	private void addError(ErrorMessage err) {
	    this.valErrorList.add(err);
	}

	public void addParsingError(String msg) {
		ErrorMessage err = new ErrorMessage(ErrorType.PARSINGERROR, msg);
		addError(err);
	}
	protected void addNoDataError(String msg) {
		ErrorMessage err = new ErrorMessage(ErrorType.NODATA, msg);
        addError(err);
	}
	protected void addWrongTypeError(String s) {
		ErrorMessage err = new ErrorMessage(ErrorType.WRONGTYPE, String.format("wrong type - %s", s));
        addError(err);
	}
    protected void addNoDataError() {
        ErrorMessage err = new ErrorMessage(ErrorType.NODATA, "no data");
        addError(err);
    }
	protected void addDuplicateFieldError(String msg) {
		ErrorMessage err = new ErrorMessage(ErrorType.DUPLICATEFIELD, msg);
        addError(err);
	}
	protected void addMissingFieldError(String msg) {
		ErrorMessage err = new ErrorMessage(ErrorType.MISSINGFIELD, msg);
        addError(err);
	}
	protected void addUnknownFieldError(String msg) {
		ErrorMessage err = new ErrorMessage(ErrorType.UNKNOWNFIELD, msg);
        addError(err);
	}
	protected void addRefError(String msg) {
		ErrorMessage err = new ErrorMessage(ErrorType.REFERROR, msg);
        addError(err);
	}

	public DType getType() {
		return type;
	}
}