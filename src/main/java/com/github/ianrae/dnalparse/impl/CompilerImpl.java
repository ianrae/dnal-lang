package com.github.ianrae.dnalparse.impl;

import java.util.List;

import org.dnal.core.DTypeRegistry;
import org.dnal.core.DTypeRegistryBuilder;
import org.dnal.core.ErrorMessage;
import org.dnal.core.repository.MockRepositoryFactory;
import org.dnal.core.repository.MyWorld;

import com.github.ianrae.dnalparse.CompilerOptions;
import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.DataSet;
import com.github.ianrae.dnalparse.dnalgenerate.CustomRuleFactory;
import com.github.ianrae.dnalparse.dnalgenerate.RuleFactory;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.generate.GenerateVisitor;
import com.github.ianrae.dnalparse.impoter.DefaultImportLoader;
import com.github.ianrae.dnalparse.impoter.ImportLoader;
import com.github.ianrae.dnalparse.impoter.MockImportLoader;
import com.github.ianrae.dnalparse.nrule.StandardRuleFactory;

/**
 * The public compiler for DNAL
 * 
 * @author ian
 *
 */
public class CompilerImpl implements DNALCompiler {
//    private List<ErrorMessage> errL = new ArrayList<>();
    private XErrorTracker et = new XErrorTracker();
    private CustomRuleFactory crf;
    private MyWorld world = new MyWorld();
    private DTypeRegistry registry;
    private CompilerContext context;
//    private boolean useMockImportLoader = false;
    private CompilerOptions compilerOptions = new CompilerOptions();

    public CompilerImpl() {
        StandardRuleFactory standard = new StandardRuleFactory();
        crf = standard.createFactory();
        world.setRepositoryFactory(new MockRepositoryFactory());
        DTypeRegistryBuilder regBuilder = new DTypeRegistryBuilder();
        regBuilder.init(world);
        registry = regBuilder.getRegistry();
    }
    
    //!!fix later
    public CompilerContext getContext() {
        if (context == null) {
            compilerOptions.useProxyDValues(true);
            ImportLoader loader = (compilerOptions.isUseMockImportLoader()) ? new MockImportLoader() : new DefaultImportLoader();
            String dir = "src/main/resources/test/example"; //!!
            context = new CompilerContext("", 0, loader, dir, compilerOptions);
            context.et = et;
        }
        return context;
    }

    @Override
    public void registryRuleFactory(RuleFactory factory) {
        crf.addFactory(factory);
    }

    @Override
    public DataSet compile(String path) {
        return compile(path, null);
    }
    @Override
    public DataSet compile(String path, GenerateVisitor visitor) {
        getContext();
        context.perf.startTimer("compile");
        SourceCompiler inner = new SourceCompiler(world, registry, crf, et, getContext());

        DataSet dataSet = inner.compile(path, visitor);
//        errL = inner.getErrors();
        context.perf.endTimer("compile");
        return dataSet;
    }

    @Override
    public DataSet compileString(String input) {
        return compileString(input, null);
    }
    @Override
    public DataSet compileString(String input, GenerateVisitor visitor) {
        getContext();
        context.perf.startTimer("compile-string");
        SourceCompiler inner = new SourceCompiler(world, registry, crf, et, getContext());

        DataSet dataSet = inner.compileString(input, visitor);
        context.perf.endTimer("compile-string");
//        errL = inner.getErrors();
        return dataSet;
    }

    @Override
    public List<ErrorMessage> getErrors() {
        return et.getErrL();
    }

    @Override
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

}