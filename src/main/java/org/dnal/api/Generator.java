package org.dnal.api;

import java.util.List;

import org.dnal.compiler.generate.TypeGenerator;
import org.dnal.compiler.generate.ValueGenerator;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;

public interface Generator {
    
     boolean generateTypes(TypeGenerator visitor);
     boolean generateValues(ValueGenerator visitor);
     boolean generateValue(ValueGenerator visitor, DValue dval, String valueName);
     List<NewErrorMessage> getErrors();

}