package org.dval.builder;

import java.util.List;

import org.dval.DType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldbuilder.XLongValueBuilder;

public class LongBuilder extends Builder {
    private XLongValueBuilder builder;
    
    public LongBuilder(DType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XLongValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(Long lval) {
        builder.buildFrom(lval);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}