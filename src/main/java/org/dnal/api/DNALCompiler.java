package org.dnal.api;

import java.util.List;

import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.compiler.generate.GenerateVisitor;
import org.dnal.core.ErrorMessage;
import org.dnal.core.NewErrorMessage;

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
    List<NewErrorMessage> getErrors();

}