package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.oldbuilder.XBooleanValueBuilder;

public class BooleanBuilder extends Builder {
    private XBooleanValueBuilder builder;
    
    public BooleanBuilder(DType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XBooleanValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(Boolean b) {
        builder.buildFrom(b);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}