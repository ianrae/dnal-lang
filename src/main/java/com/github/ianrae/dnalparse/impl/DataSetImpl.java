package com.github.ianrae.dnalparse.impl;

import java.util.HashMap;
import java.util.Map;

import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.MyWorld;

import com.github.ianrae.dnalparse.DValueLoader;
import com.github.ianrae.dnalparse.DataSet;
import com.github.ianrae.dnalparse.Generator;
import com.github.ianrae.dnalparse.Transaction;

public class DataSetImpl implements DataSet {
    private MyWorld world;
    private DTypeRegistry registry;
    private Map<Class<?>, DValueLoader<?>> loaderRegistry = new HashMap<>();
    private CompilerContext context;
    
    public DataSetImpl(DTypeRegistry registry, MyWorld listener, CompilerContext context) {
        this.registry = registry;
        this.world = listener;
        this.context = context;
    }
    
    public Internals getInternals() {
        return new Internals(registry, world);
    }
    
    @Override
    public Transaction createTransaction() {
        return new TransactionImpl(registry, world, context, loaderRegistry);
    }
    
    @Override
    public void registerLoader(DValueLoader<?> loader) {
        Class<?> clazz = loader.willLoad();
        this.loaderRegistry.put(clazz, loader);
        loader.attach(registry, world, context);
    }
    
    @Override
    public DValue getValue(String varName) {
        return world.findTopLevelValue(varName);
    }
    
    @Override
    public <T> T getAsBean(String varName, Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        DValueLoader<T> loader = (DValueLoader<T>) loaderRegistry.get(clazz);
        if (loader == null) {
            return null;
        }
        DValue dval = getValue(varName);
        if (dval == null) {
            return null;
        }
        
        T bean = loader.create(dval);
        return bean;
    }

    @Override
    public Generator createGenerator() {
        Generator generator = new GeneratorImpl(registry, world, context);
        return generator;
    }
    
    /**
     * Return a new data set with same types as this data set,
     * but no values.
     * @return
     */
    @Override
    public DataSetImpl cloneEmptyDataSet() {
        MyWorld newWorld = new MyWorld();
        newWorld.setRepositoryFactory(world.getRepositoryFactory());
        for(String typeName: registry.getAll()) {
            DType type = registry.getType(typeName);
            newWorld.typeRegistered(type);
        }
        DataSetImpl clone = new DataSetImpl(registry, newWorld, context);
        return clone;
    }
    
    @Override
    public int size() {
        return world.getValueMap().size();
    }

}