package org.dnal.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.CompilerOptions;
import org.dnal.compiler.dnalgenerate.CustomRuleFactory;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.impoter.ImportLoader;
import org.dnal.compiler.impoter.PackageRepository;
import org.dnal.compiler.performance.PerfTimer;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.ErrorMessage;
import org.dnal.core.repository.World;


public class CompilerContext {
    
    //set manually
    public List<ErrorMessage> errL = new ArrayList<>();
    public World world;
    public DTypeRegistry registry;
    public CustomRuleFactory crf;
//    public ErrorScopeStack scopeStack = new ErrorScopeStack();
    public PerfTimer perf = new PerfTimer();
    public CompilerOptions compilerOptions;

    //passed in
    public String packageName;
    public Integer runawayCounter;
    public PackageRepository prepo;
    public ImportLoader loader;
    public String sourceDir;
    public XErrorTracker et;
    
    public CompilerContext(String packageName, Integer runawayCounter, ImportLoader loader, 
            String sourceDir, CompilerOptions options) {
        this.packageName = packageName;
        this.runawayCounter = runawayCounter;
        this.loader = loader;
        this.prepo = new PackageRepository();
        this.sourceDir = sourceDir;
        this.compilerOptions = options;
    }

}