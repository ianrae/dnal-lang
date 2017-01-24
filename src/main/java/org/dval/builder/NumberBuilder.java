package org.dval.builder;

import java.util.List;

import org.dval.DType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldbuilder.XNumberValueBuilder;

public class NumberBuilder extends Builder {
    private XNumberValueBuilder builder;
    
    public NumberBuilder(DType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XNumberValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(double lval) {
        builder.buildFrom(lval);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}