package com.github.ianrae.dnalparse;

import java.util.List;

import org.dval.ErrorMessage;

import com.github.ianrae.dnalparse.dnalgenerate.RuleFactory;
import com.github.ianrae.dnalparse.generate.GenerateVisitor;

/**
 * The public compiler for DNAL
 * 
 * @author ian
 *
 */
public interface DNALCompiler {
    
//    void useMockImportLoader(boolean b);
    CompilerOptions getCompilerOptions();
    void registryRuleFactory(RuleFactory factory);
    DataSet compile(String path);
    DataSet compile(String path, GenerateVisitor visitor);
    DataSet compileString(String input);
    DataSet compileString(String input, GenerateVisitor visitor);
    List<ErrorMessage> getErrors();

}