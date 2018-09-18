package org.dnal.api;

import java.io.InputStream;
import java.util.List;

import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.compiler.generate.old.OldOutputGenerator;
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
    DataSet compile(String path, OldOutputGenerator visitor);
    DataSet compile(InputStream stream);
    DataSet compile(InputStream stream, OldOutputGenerator visitor);
    DataSet compileString(String input);
    DataSet compileString(String input, OldOutputGenerator visitor);
    List<NewErrorMessage> getErrors();
    String formatError(NewErrorMessage err);

}