package org.dnal.api;

import java.io.InputStream;
import java.util.List;

import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.compiler.generate.OuputGenerator;
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
    DataSet compile(String path, OuputGenerator visitor);
    DataSet compile(InputStream stream);
    DataSet compile(InputStream stream, OuputGenerator visitor);
    DataSet compileString(String input);
    DataSet compileString(String input, OuputGenerator visitor);
    List<NewErrorMessage> getErrors();

}