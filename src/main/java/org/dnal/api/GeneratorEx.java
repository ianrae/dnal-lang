package org.dnal.api;

import java.util.List;

import org.dnal.core.NewErrorMessage;
import org.dnal.outputex.TypeGeneratorEx;
import org.dnal.outputex.ValueGeneratorEx;

public interface GeneratorEx {
    
     boolean generateTypes(TypeGeneratorEx visitor);
     boolean generateValues(ValueGeneratorEx visitor);
     List<NewErrorMessage> getErrors();

}