package com.github.ianrae.dnalparse.builder;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.repository.WorldAdder;

public class BufferingWorldAdder implements WorldAdder {
    public List<DValue> addedValueL = new ArrayList<>();
    
    @Override
    public void valueAdded(DValue dval) {
        addedValueL.add(dval);
    }

}
