package com.github.ianrae.dnalparse;

import java.util.List;

import org.dnal.compiler.generate.GenerateVisitor;
import org.dnal.core.ErrorMessage;

public interface Generator {
    
     boolean generate(GenerateVisitor visitor);
     List<ErrorMessage> getErrors();

}