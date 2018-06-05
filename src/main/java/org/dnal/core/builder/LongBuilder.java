package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.xbuilder.XLongValueBuilder;

public class LongBuilder extends Builder {
    private XLongValueBuilder builder;
    
    public LongBuilder(DType type, List<NewErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XLongValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(Long lval) {
        builder.buildFrom(lval);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}