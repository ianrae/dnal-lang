package org.dnal.api.bean;

import org.dnal.api.DataSet;
import org.dnal.api.OldGenerator;
import org.dnal.api.GeneratorEx;
import org.dnal.api.Transaction;
import org.dnal.api.WorldException;
import org.dnal.api.impl.CompilerImpl;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.generate.DNALTypeGeneratorEx;
import org.dnal.compiler.generate.DNALValueGeneratorEx;
import org.dnal.compiler.generate.SimpleFormatOutputGeneratorEx;
import org.dnal.compiler.generate.old.DNALOutputGenerator;
import org.dnal.compiler.generate.old.SimpleFormatOutputGenerator;
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
        createCompilerIfNeeded();
        mainDataSet = compiler.compile(dnalPath);
        if (mainDataSet == null) {
            return false;
        }
        doBeginNewDataSet(false);
        return true;
    }
    public boolean loadTypeDefinitionFromString(String source) {
        createCompilerIfNeeded();
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
    
    private void createCompilerIfNeeded() {
    	if (compiler == null) {
    		compiler = new CompilerImpl();
    		CompilerImpl impl = (CompilerImpl) compiler;
    		et = impl.getContext().et;
    	}
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
    	
        SimpleFormatOutputGeneratorEx smf = new SimpleFormatOutputGeneratorEx();
        GeneratorEx gen = clone.createGeneratorEx();
        boolean b = gen.generateTypes(smf);
        if (! b) {
            return;
        }
        for(String ss: smf.outputL) {
            log(ss);
        }
        
        b = gen.generateValues(smf);
        if (! b) {
            return;
        }
        for(String ss: smf.outputL) {
            log(ss);
        }
    }
    public void dumpAsDNAL() {
        DNALTypeGeneratorEx smf = new DNALTypeGeneratorEx();
        GeneratorEx gen = clone.createGeneratorEx();
        boolean b = gen.generateTypes(smf);
        if (! b) {
            return;
        }
        for(String ss: smf.outputL) {
            log(ss);
        }
        
        DNALValueGeneratorEx smf2 = new DNALValueGeneratorEx();
        b = gen.generateValues(smf2);
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
    	//capture transaction errors and add back to main error list
        boolean b = trans.commit();
        et.propogateErrors(trans.getValErrorList());
        return b;
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
	public void initCompiler() {
		compiler = null;
		createCompilerIfNeeded();
	}
}