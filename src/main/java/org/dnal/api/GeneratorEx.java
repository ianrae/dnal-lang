package org.dnal.api;

import java.util.List;

import org.dnal.core.NewErrorMessage;
import org.dnal.outputex.OutputGeneratorEx;

public interface GeneratorEx {
    
     boolean generate(OutputGeneratorEx visitor);
     List<NewErrorMessage> getErrors();

}