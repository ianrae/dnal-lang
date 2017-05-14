package org.dnal.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jparsec.functors.Pair;
import org.dnal.api.BeanLoader;
import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.WorldException;
import org.dnal.api.impl.CompilerContext;
import org.dnal.compiler.dnalgenerate.CustomRuleFactory;
import org.dnal.compiler.nrule.StandardRuleFactory;
import org.dnal.compiler.validate.ValidationOptions;
import org.dnal.compiler.validate.ValidationPhase;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.DViewType;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.builder.BooleanBuilder;
import org.dnal.core.builder.BuilderFactory;
import org.dnal.core.builder.DateBuilder;
import org.dnal.core.builder.EnumBuilder;
import org.dnal.core.builder.IntBuilder;
import org.dnal.core.builder.ListBuilder;
import org.dnal.core.builder.LongBuilder;
import org.dnal.core.builder.NumberBuilder;
import org.dnal.core.builder.StringBuilder;
import org.dnal.core.builder.StructBuilder;
import org.dnal.core.repository.World;

public class TransactionImpl implements Transaction {
    protected List<NewErrorMessage> errorList = new ArrayList<>();
    private DTypeRegistry registry;
    private World world;
    private List<Pair<String, DValue>> pendingL = new ArrayList<>();
    private BuilderFactory factory;
    private CompilerContext context;
    private Map<Class<?>, BeanLoader<?>> loaderRegistry;
    private DataSet ds;
    private ValidationOptions validateOptions; //local to the trans
	private List<DValue> futureValues = new ArrayList<>();
    

    public TransactionImpl(DTypeRegistry registry, World world, CompilerContext context, Map<Class<?>, BeanLoader<?>> loaderRegistry, DataSet ds) {
        this.world = world;
        this.registry = registry;
        this.factory = new BuilderFactory(registry, errorList);
        this.context = context;
        this.loaderRegistry = loaderRegistry;
        this.ds = ds;
        this.validateOptions = context.validateOptions.createCopy(); //create local copy
    }

    @Override
    public void add(String name, DValue dval) {
        if (dval == null || name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name or dval were null");
        }
        if (dval.getType() instanceof DViewType) {
        	throw new IllegalArgumentException("view types cannot be added to a DataSet");
        }
        pendingL.add(new Pair<String, DValue>(name, dval));
    }

    //eventually add update and remove which will be handled using event log approach
    @Override
    public boolean commit() {
    	context.et.propogateErrors(errorList);
    	
        //validate
        for(Pair<String,DValue> pair: pendingL) {
            if (! validateSingleValue(pair)) {
                return false;
            }
        }

        //everything is valid, so add to world
        for(Pair<String,DValue> pair: pendingL) {
            String name = pair.a;
            DValue dval = pair.b;
            //add all sub-vals
            AddObserver observer = new AddObserver(world);
            observer.observe(dval);

            world.addTopLevelValue(name, dval);
        }
        return true;
    }

    private boolean validateSingleValue(Pair<String,DValue> pair) {
        ValidationPhase validator = new ValidationPhase(world, context.et, validateOptions, futureValues);

        DValue dval = pair.b;
        String varName = pair.a;
        boolean b = validator.validateDValue(varName, dval, dval.getType());
        return b;
    }

    @Override
    public List<NewErrorMessage> getValErrorList() {
        return errorList;
    }

    @Override
    public DValue createFromBean(Object bean) throws WorldException {
        if (bean == null) {
            throw new WorldException("null passed to createFromBean()");
        }
        
        BeanLoader<?> loader = (BeanLoader<?>) loaderRegistry.get(bean.getClass());
        if (loader == null) {
            throw new WorldException(String.format("bean class '%s' not registered. Use loadRegister()", bean.getClass().getSimpleName()));
        }
        
        DValue dval = loader.createDValue(bean);
        return dval;
    }
    
    

    //-------------- builder ------------------------
    @Override
    public DType getType(String typeName) {
        DType structType = registry.getType(typeName);
        return structType;
    }
    @Override
    public DListType getListType(String typeName) {
        DListType listType = (DListType) registry.getType(typeName);
        return listType;
    }
    @Override
    public DStructType getStructType(String typeName) {
        DStructType structType = (DStructType) registry.getType(typeName);
        return structType;
    }

    @Override
    public StructBuilder createStructBuilder(DStructType structType) {
        return factory.createStructBuilder(structType);
    }
    @Override
    public StructBuilder createStructBuilder(String typeName) {
        return factory.createStructBuilder(typeName);
    }

    @Override
    public IntBuilder createIntBuilder() {
        return factory.createIntegerBuilder();
    }
    @Override
    public IntBuilder createIntBuilder(String typeName) {
        return factory.createIntegerBuilder(typeName);
    }
    @Override
    public IntBuilder createIntBuilder(DType type) {
        return factory.createIntegerBuilder(type);
    }

    @Override
    public LongBuilder createLongBuilder() {
        return factory.createLongBuilder();
    }
    @Override
    public LongBuilder createLongBuilder(String typeName) {
        return factory.createLongBuilder(typeName);
    }
    @Override
    public LongBuilder createLongBuilder(DType type) {
        return factory.createLongBuilder(type);
    }

    @Override
    public BooleanBuilder createBooleanBuilder() {
        return factory.createBooleanBuilder();        
    }
    @Override
    public BooleanBuilder createBooleanBuilder(String typeName) {
        return factory.createBooleanBuilder(typeName);
    }
    @Override
    public BooleanBuilder createBooleanBuilder(DType type) {
        return factory.createBooleanBuilder(type);
    }

    @Override
    public NumberBuilder createNumberBuilder() {
        return factory.createNumberBuilder();
    }
    @Override
    public NumberBuilder createNumberBuilder(String typeName) {
        return factory.createNumberBuilder(typeName);
    }
    @Override
    public NumberBuilder createNumberBuilder(DType type) {
        return factory.createNumberBuilder(type);
    }

    @Override
    public DateBuilder createDateBuilder() {
        return factory.createDateBuilder();
    }
    @Override
    public DateBuilder createDateBuilder(String typeName) {
        return factory.createDateBuilder(typeName);
    }
    @Override
    public DateBuilder createDateBuilder(DType type) {
        return factory.createDateBuilder(type);
    }

    @Override
    public StringBuilder createStringBuilder() {
        return factory.createStringBuilder();
    }
    @Override
    public StringBuilder createStringBuilder(String typeName) {
        return factory.createStringBuilder(typeName);
    }
    @Override
    public StringBuilder createStringBuilder(DType type) {
        return factory.createStringBuilder(type);
    }

    @Override
    public EnumBuilder createEnumBuilder(String typeName) {
        return factory.createEnumBuilder(typeName);
    }
    @Override
    public EnumBuilder createEnumBuilder(DType type) {
        return factory.createEnumBuilder(type);
    }

    //for list builder the list elements should be built with lower-level DValueBuilder, assembled into the list
    //then added to the world all at once. DValueBuilders all should use same BuffereingWorldAdder
    @Override
    public ListBuilder createListBuilder(String typeName) {
        return factory.createListBuilder(typeName);
    }
    @Override
    public ListBuilder createListBuilder(DListType type) {
        return factory.createListBuilder(type);
    }

	@Override
	public DataSet getDataSet() {
		return ds;
	}

	@Override
	public ValidationOptions getValidationOptions() {
		return this.validateOptions;
	}

	public List<DValue> getFutureValues() {
		return futureValues;
	}

}
