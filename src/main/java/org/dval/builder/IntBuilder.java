package org.dval.builder;

import java.util.List;

import org.dval.DType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldbuilder.XIntegerValueBuilder;

public class IntBuilder extends Builder {
    private XIntegerValueBuilder builder;
    
    public IntBuilder(DType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XIntegerValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(Integer lval) {
        builder.buildFrom(lval);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}