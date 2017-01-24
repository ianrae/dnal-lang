package org.dval.builder;

import java.util.List;

import org.dval.DListType;
import org.dval.DValue;
import org.dval.ErrorMessage;
import org.dval.oldbuilder.XListValueBuilder;


public class ListBuilder extends Builder {
    private XListValueBuilder builder;
    
    public ListBuilder(DListType type, List<ErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XListValueBuilder(type);
    }

    public void addElement(DValue dval) {
        builder.addValue(dval);
    }

    public DValue finish() {
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
}