package org.dnal.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.api.BeanLoader;
import org.dnal.api.DataSet;
import org.dnal.api.Generator;
import org.dnal.api.Transaction;
import org.dnal.compiler.dnalgenerate.ViaFinder;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.World;

public class DataSetImpl implements DataSet {
    private World world;
    private DTypeRegistry registry;
    private Map<Class<?>, BeanLoader<?>> beanLoaderRegistry = new HashMap<>();
    private CompilerContext context;
    
    public DataSetImpl(DTypeRegistry registry, World listener, CompilerContext context) {
        this.registry = registry;
        this.world = listener;
        this.context = context;
    }
    
    public Internals getInternals() {
        return new Internals(registry, world);
    }
    public CompilerContext getCompilerContext() {
    	return context;
    }
    
    @Override
    public Transaction createTransaction() {
        return new TransactionImpl(registry, world, context, beanLoaderRegistry, this);
    }
    
    @Override
    public void registerBeanLoader(BeanLoader<?> loader) {
        Class<?> clazz = loader.willLoad();
        this.beanLoaderRegistry.put(clazz, loader);
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
        BeanLoader<T> loader = (BeanLoader<T>) beanLoaderRegistry.get(clazz);
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
        World newWorld = new World();
        newWorld.setRepositoryFactory(world.getRepositoryFactory());
        for(String typeName: registry.getAll()) {
            DType type = registry.getType(typeName);
            newWorld.typeRegistered(type);
        }
        DataSetImpl clone = new DataSetImpl(registry, newWorld, context);
        return clone;
    }
    
    /**
     * Return a new data set with same types as this data set,
     * and all its values. Note the new dataset has direct references
     * to the dvals of this data set, so we are assuming that
     * dvals are immutable.
     * 
     * @return
     */
    @Override
    public DataSetImpl cloneDataSet() {
        World newWorld = new World();
        newWorld.setRepositoryFactory(world.getRepositoryFactory());
        
        for(String typeName: registry.getAll()) {
            DType type = registry.getType(typeName);
            newWorld.typeRegistered(type);
        }
        for(String varName: world.getValueMap().keySet()) {
        	DValue dval = world.getValueMap().get(varName);
        	newWorld.addTopLevelValue(varName, dval);
        }
        
        DataSetImpl clone = new DataSetImpl(registry, newWorld, context);
        return clone;
    }
    
    @Override
    public int size() {
        return world.getValueMap().size();
    }

	@Override
	public List<String> getAllNames() {
		//return copy so app can't tamper with world
		List<String> copy = new ArrayList(world.getOrderedList());
		return copy;
	}
	
	@Override
    public DType getType(String typeName) {
    	DType dtype = this.registry.getType(typeName);
    	return dtype;
    }
	
	public ViaFinder createViaFinder() {
		ViaFinder finder = new ViaFinder(world, registry, context.et);
		return finder;
	}

}