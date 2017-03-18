package org.dnal.api.bean;

import org.dnal.api.DataSet;
import org.dnal.api.Generator;
import org.dnal.api.Transaction;
import org.dnal.api.WorldException;
import org.dnal.api.impl.CompilerImpl;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.generate.DNALVisitor;
import org.dnal.compiler.generate.SimpleMinimumFormatVisitor;
import org.dnal.core.DValue;

public class DNALLoader {
    protected CompilerImpl compiler;
    private DataSet mainDataSet;
    private DataSet clone;
    private Transaction trans;
    private ReflectionBeanLoader reflectionBeanLoader;
    private XErrorTracker et;
    private FieldConverter fieldConverter = null;
    private boolean cloneMainDataSet = true;
    
    public boolean loadTypeDefinition(String dnalPath) {
        createCompiler();
        mainDataSet = compiler.compile(dnalPath);
        if (mainDataSet == null) {
            return false;
        }
        doBeginNewDataSet(false);
        return true;
    }
    public boolean loadTypeDefinitionFromString(String source) {
        createCompiler();
        mainDataSet = compiler.compileString(source);
        if (mainDataSet == null) {
            return false;
        }
        doBeginNewDataSet(false);
        return true;
    }
    public void beginNewDataSet() {
    	doBeginNewDataSet(true);
    }
    public void doBeginNewDataSet(boolean clearErrors) {
    	if (clearErrors) {
    		et.clear(); //remove any previous errors
    	}
    	if (cloneMainDataSet) {
    		clone = mainDataSet.cloneEmptyDataSet();
    	} else {
    		clone = mainDataSet;
    	}
        trans = clone.createTransaction();
    }
    
    
    public XErrorTracker getErrorTracker() {
    	return compiler.getContext().et;
    }
    
    private void createCompiler() {
        compiler = new CompilerImpl();
        CompilerImpl impl = (CompilerImpl) compiler;
        et = impl.getContext().et;
    }
    
    
    public DataSet getDataSet() {
        return clone;
    }
    
    public void dumpErrors() {
    	et.dumpErrors();
    }
    
    public void dump() {
    	if (mainDataSet == null) {
    		et.dumpErrors();
    	}
    	
        SimpleMinimumFormatVisitor smf = new SimpleMinimumFormatVisitor();
        Generator gen = clone.createGenerator();
        boolean b = gen.generate(smf);
        if (! b) {
            return;
        }
        for(String ss: smf.outputL) {
            log(ss);
        }
    }
    public void dumpAsDNAL() {
        DNALVisitor smf = new DNALVisitor();
        Generator gen = clone.createGenerator();
        boolean b = gen.generate(smf);
        if (! b) {
            return;
        }
        for(String ss: smf.outputL) {
            log(ss);
        }
    }
    private void log(String s) {
        System.out.println(s);
    }
    
    public DValue createFromBean(String typeName, Object bean) throws WorldException {
        if (bean == null) {
            throw new WorldException("null passed to createFromBean()");
        }
       reflectionBeanLoader = new ReflectionBeanLoader(typeName, clone, et, fieldConverter);
        
        DValue dval = reflectionBeanLoader.createDValue(bean);
        return dval;
    }
    public void addTopLevelValue(String name, DValue dval) {
        trans.add(name, dval);
    }
    public boolean commit() {
        return trans.commit();
    }
	public FieldConverter getFieldConverter() {
		return fieldConverter;
	}
	public void setFieldConverter(FieldConverter fieldConverter) {
		this.fieldConverter = fieldConverter;
	}
	public boolean isCloneMainDataSet() {
		return cloneMainDataSet;
	}
	public void setCloneMainDataSet(boolean cloneMainDataSet) {
		this.cloneMainDataSet = cloneMainDataSet;
	}
}