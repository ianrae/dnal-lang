package com.github.ianrae.dnalparse.compiler;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.DTypeRegistryBuilder;
import org.dnal.core.ErrorMessage;
import org.dnal.core.logger.Log;
import org.dnal.core.repository.MockRepositoryFactory;
import org.dnal.core.repository.MyWorld;

import com.github.ianrae.dnalparse.CompilerOptions;
import com.github.ianrae.dnalparse.dnalgenerate.ASTToDNALGenerator;
import com.github.ianrae.dnalparse.dnalgenerate.CustomRuleFactory;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.impl.CompilerContext;
import com.github.ianrae.dnalparse.impoter.MockImportLoader;
import com.github.ianrae.dnalparse.nrule.StandardRuleFactory;

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
