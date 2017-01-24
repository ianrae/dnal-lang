package org.dval.builder;

import java.util.List;

import org.dval.DType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldbuilder.XStringValueBuilder;

public class StringBuilder extends Builder {
    private XStringValueBuilder builder;
    
    public StringBuilder(DType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XStringValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}