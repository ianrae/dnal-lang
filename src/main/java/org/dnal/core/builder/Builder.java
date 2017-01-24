package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.ErrorMessage;

public class Builder {
    protected List<ErrorMessage> valErrorList;
    protected String dateFormat = "dd-MMM-yy";

    public Builder(List<ErrorMessage> valErrorList) {
        this.valErrorList = valErrorList;
    }
    public List<ErrorMessage> getValidationErrors() {
        return valErrorList;
    }
    public String getDateFormat() {
        return dateFormat;
    }
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}