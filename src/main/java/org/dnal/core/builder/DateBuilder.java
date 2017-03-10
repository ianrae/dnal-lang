package org.dnal.core.builder;

import java.util.Date;
import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.oldbuilder.XDateValueBuilder;


public class DateBuilder extends Builder {
    private XDateValueBuilder builder;
    
    public DateBuilder(DType type, List<NewErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XDateValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(Date dt) {
        builder.buildFrom(dt);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}