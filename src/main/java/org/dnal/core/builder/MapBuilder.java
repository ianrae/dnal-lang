package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DMapType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.xbuilder.XMapValueBuilder;


public class MapBuilder extends Builder {
    private XMapValueBuilder builder;
    
    public MapBuilder(DMapType type, List<NewErrorMessage> valErrorList) {
        super(valErrorList);
        builder = new XMapValueBuilder(type);
    }

    public void addElement(String key, DValue dval) {
        builder.addElement(key, dval);
    }

    public DValue finish() {
        wasSuccessful = builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
}