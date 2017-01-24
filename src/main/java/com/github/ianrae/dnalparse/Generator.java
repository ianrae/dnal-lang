package com.github.ianrae.dnalparse;

import java.util.List;

import org.dnal.core.ErrorMessage;

import com.github.ianrae.dnalparse.generate.GenerateVisitor;

public interface Generator {
    
     boolean generate(GenerateVisitor visitor);
     List<ErrorMessage> getErrors();

}