package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.oldbuilder.XNumberValueBuilder;

public class NumberBuilder extends Builder {
    private XNumberValueBuilder builder;
    
    public NumberBuilder(DType type, List<NewErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XNumberValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(Double lval) {
        builder.buildFrom(lval);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}