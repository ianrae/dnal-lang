package org.dnal.api;

import java.io.InputStream;
import java.util.List;

import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.compiler.generate.OutputGenerator;
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
    DataSet compile(String path, OutputGenerator visitor);
    DataSet compile(InputStream stream);
    DataSet compile(InputStream stream, OutputGenerator visitor);
    DataSet compileString(String input);
    DataSet compileString(String input, OutputGenerator visitor);
    List<NewErrorMessage> getErrors();
    String formatError(NewErrorMessage err);

}