package org.dnal.api;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.outputex.TypeGeneratorEx;
import org.dnal.outputex.ValueGeneratorEx;

public interface GeneratorEx {
    
     boolean generateTypes(TypeGeneratorEx visitor);
     boolean generateValues(ValueGeneratorEx visitor);
     boolean generateValue(ValueGeneratorEx visitor, DValue dval, String valueName);
     List<NewErrorMessage> getErrors();

}