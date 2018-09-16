package org.dnal.api;

import java.util.List;

import org.dnal.core.NewErrorMessage;
import org.dnal.outputex.OutputGeneratorEx;
import org.dnal.outputex.OutputOptions;

public interface GeneratorEx {
    
     boolean generate(OutputGeneratorEx visitor, OutputOptions options);
     List<NewErrorMessage> getErrors();

}