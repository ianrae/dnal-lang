package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.oldbuilder.XEnumValueBuilder;


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