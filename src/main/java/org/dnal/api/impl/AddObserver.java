package org.dnal.api.impl;

import java.util.List;

import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.repository.WorldAdder;

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
        	//scalars don't have any sub-values
        } else if (dval.getType().isStructShape()) {
            DStructHelper helper = new DStructHelper(dval);
            for(String fieldName: helper.getFieldNames()) {
                DValue field = helper.getField(fieldName);
                //ignore optional fields
                if (field != null) {
                	doObserve(field, true); //**recursion**
                }
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
