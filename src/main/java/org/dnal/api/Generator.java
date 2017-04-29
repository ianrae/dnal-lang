package org.dnal.api;

import java.util.List;

import org.dnal.compiler.generate.OuputGenerator;
import org.dnal.core.NewErrorMessage;

public interface Generator {
    
     boolean generate(OuputGenerator visitor);
     List<NewErrorMessage> getErrors();

}