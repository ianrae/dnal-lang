package org.dnal.api;

import java.util.List;

import org.dnal.compiler.generate.OutputGenerator;
import org.dnal.core.NewErrorMessage;

public interface Generator {
    
     boolean generate(OutputGenerator visitor);
     List<NewErrorMessage> getErrors();

}