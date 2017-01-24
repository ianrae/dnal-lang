package com.github.ianrae.dnalparse.impl;

import java.util.List;

import org.dval.DStructHelper;
import org.dval.DValue;
import org.dval.Shape;
import org.dval.repository.WorldAdder;

public class AddObserver {

    private WorldAdder adder;

    public AddObserver(WorldAdder adder) {
        this.adder = adder;
    }

    //will not handle circular references!!
    public void observe(DValue dval) {
        doObserve(dval, false);
    }
    
    private void doObserve(DValue dval, boolean doAdd) {
        if (doAdd) {
            adder.valueAdded(dval);
        }
        
        if (dval.getType().isScalarShape()) {
        } else if (dval.getType().isStructShape()) {
            DStructHelper helper = new DStructHelper(dval);
            for(String fieldName: helper.getFieldNames()) {
                DValue field = helper.getField(fieldName);
                doObserve(field, true); //**recursion**
            }
        } else if (dval.getType().isShape(Shape.ENUM)) {
            DStructHelper helper = new DStructHelper(dval);
            for(String fieldName: helper.getFieldNames()) {
                DValue field = helper.getField(fieldName);
                doObserve(field, true); //**recursion**
            }
        } else if (dval.getType().isShape(Shape.LIST)) {
            List<DValue> list = dval.asList();
            for(DValue el: list) {
                doObserve(el, true); //**recursion**
            }
        }
    }
}
