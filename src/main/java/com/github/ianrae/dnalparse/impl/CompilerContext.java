package com.github.ianrae.dnalparse.impl;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.DTypeRegistry;
import org.dnal.core.ErrorMessage;
import org.dnal.core.repository.MyWorld;

import com.github.ianrae.dnalparse.CompilerOptions;
import com.github.ianrae.dnalparse.dnalgenerate.CustomRuleFactory;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.impoter.ImportLoader;
import com.github.ianrae.dnalparse.impoter.PackageRepository;
import com.github.ianrae.dnalparse.performance.PerfTimer;


public class CompilerContext {
    
    //set manually
    public List<ErrorMessage> errL = new ArrayList<>();
    public MyWorld world;
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