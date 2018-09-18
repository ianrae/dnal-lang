package org.dnal.api;

import java.util.List;

import org.dnal.compiler.generate.TypeGeneratorEx;
import org.dnal.compiler.generate.ValueGeneratorEx;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;

public interface Generator {
    
     boolean generateTypes(TypeGeneratorEx visitor);
     boolean generateValues(ValueGeneratorEx visitor);
     boolean generateValue(ValueGeneratorEx visitor, DValue dval, String valueName);
     List<NewErrorMessage> getErrors();

}