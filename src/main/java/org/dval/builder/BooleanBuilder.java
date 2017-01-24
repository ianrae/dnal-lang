package org.dval.builder;

import java.util.List;

import org.dval.DType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldbuilder.XBooleanValueBuilder;

public class BooleanBuilder extends Builder {
    private XBooleanValueBuilder builder;
    
    public BooleanBuilder(DType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XBooleanValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(boolean b) {
        builder.buildFrom(b);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}