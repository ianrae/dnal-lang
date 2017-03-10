package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DListType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.oldbuilder.XListValueBuilder;


public class ListBuilder extends Builder {
    private XListValueBuilder builder;
    
    public ListBuilder(DListType type, List<NewErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XListValueBuilder(type);
    }

    public void addElement(DValue dval) {
        builder.addValue(dval);
    }

    public DValue finish() {
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
}