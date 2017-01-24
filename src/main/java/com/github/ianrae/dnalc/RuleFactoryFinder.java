package com.github.ianrae.dnalc;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;


public class RuleFactoryFinder {
    
   public List<RuleFactory> findFactories(List<String> packages) {
        List<RuleFactory> resultL = new ArrayList<>();
        FilterBuilder fb = new FilterBuilder();
        for(String packageName: packages) {
                fb.include(FilterBuilder.prefix(packageName));
        }
        try {
            doPackage(fb, resultL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultL;
    }
    private void doPackage(FilterBuilder fb, List<RuleFactory> resultL) throws Exception {
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
            .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
            .filterInputsBy(fb));      
        
        Set<Class<? extends RuleFactory>> subTypes = reflections.getSubTypesOf(RuleFactory.class);        
        for(Class<? extends RuleFactory> clazz : subTypes) {
            RuleFactory factory = clazz.newInstance();
            resultL.add(factory);
        }
    }
}