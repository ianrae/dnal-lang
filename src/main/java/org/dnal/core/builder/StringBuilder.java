package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.xbuilder.XStringValueBuilder;

public class StringBuilder extends Builder {
    private XStringValueBuilder builder;
    
    public StringBuilder(DType type, List<NewErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XStringValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}