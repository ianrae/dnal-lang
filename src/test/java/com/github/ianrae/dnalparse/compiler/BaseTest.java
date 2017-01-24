package com.github.ianrae.dnalparse.compiler;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.CompilerOptions;
import org.dnal.api.impl.CompilerContext;
import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.dnalgenerate.CustomRuleFactory;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.impoter.MockImportLoader;
import org.dnal.compiler.nrule.StandardRuleFactory;
import org.dnal.core.DTypeRegistryBuilder;
import org.dnal.core.ErrorMessage;
import org.dnal.core.logger.Log;
import org.dnal.core.repository.MockRepositoryFactory;
import org.dnal.core.repository.MyWorld;

public class BaseTest {
    protected CustomRuleFactory crf;

    //-------------
    protected ASTToDNALGenerator createASTGenerator() {
        XErrorTracker.logErrors = true;
        Log.debugLogging = true;
        MyWorld world = new MyWorld();
        world.setRepositoryFactory(new MockRepositoryFactory());
        DTypeRegistryBuilder regBuilder = new DTypeRegistryBuilder();
        regBuilder.init(world);
        List<ErrorMessage> errorL = new ArrayList<>();
        
        if (crf == null) {
            crf = getCrf();
        }
        
        String dir = "src/main/resources/test/example";
        CompilerContext context = new CompilerContext("", 0, new MockImportLoader(), dir, new CompilerOptions());
        context.et = new XErrorTracker();
        aContext = context;
        ASTToDNALGenerator dnalGenerator = new ASTToDNALGenerator(world, regBuilder.getRegistry(), context.et, crf, context);
        return dnalGenerator;
    }    
    
    private CompilerContext aContext;
    
    protected CompilerContext getContext() {
        return aContext;
    }
    
    private CustomRuleFactory getCrf() {
        StandardRuleFactory rf = new StandardRuleFactory();
        CustomRuleFactory xcrf = rf.createFactory();
        return xcrf;
    }
    

}
