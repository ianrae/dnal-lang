package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.oldbuilder.XIntegerValueBuilder;

public class IntBuilder extends Builder {
    private XIntegerValueBuilder builder;
    
    public IntBuilder(DType type, List<NewErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XIntegerValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(Integer lval) {
        builder.buildFrom(lval);
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}