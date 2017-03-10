package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.NewErrorMessage;

public class Builder {
    protected List<NewErrorMessage> valErrorList;
    protected String dateFormat = "dd-MMM-yy";
    protected boolean wasSuccessful;

    public Builder(List<NewErrorMessage> valErrorList) {
        this.valErrorList = valErrorList;
    }
    public List<NewErrorMessage> getValidationErrors() {
        return valErrorList;
    }
    public String getDateFormat() {
        return dateFormat;
    }
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    public boolean wasSuccessful() {
    	return wasSuccessful;
    }
}