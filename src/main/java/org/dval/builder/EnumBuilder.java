package org.dval.builder;

import java.util.List;

import org.dval.DType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldbuilder.XEnumValueBuilder;


public class EnumBuilder extends Builder {
    private XEnumValueBuilder builder;
    
    public EnumBuilder(DType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XEnumValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}