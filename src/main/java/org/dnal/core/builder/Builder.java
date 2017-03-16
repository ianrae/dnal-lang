package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.NewErrorMessage;

public class Builder {
    protected List<NewErrorMessage> valErrorList;
    protected boolean wasSuccessful;

    public Builder(List<NewErrorMessage> valErrorList) {
        this.valErrorList = valErrorList;
    }
    public List<NewErrorMessage> getValidationErrors() {
        return valErrorList;
    }
    public boolean wasSuccessful() {
    	return wasSuccessful;
    }
}