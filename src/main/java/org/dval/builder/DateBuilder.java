package org.dval.builder;

import java.util.Date;
import java.util.List;

import org.dval.DType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldbuilder.XDateValueBuilder;


public class DateBuilder extends Builder {
    private XDateValueBuilder builder;
    
    public DateBuilder(DType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XDateValueBuilder(type);
    }

    public DValue buildFromString(String input) {
        builder.buildFromString(input);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    public DValue buildFrom(Date dt) {
        builder.buildFrom(dt);
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }

}