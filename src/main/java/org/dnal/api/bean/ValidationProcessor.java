package org.dnal.api.bean;

import java.util.List;

import org.dnal.api.WorldException;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;

public class ValidationProcessor {
    DNALLoader dnalLoader;
    
    public ValidationProcessor() {
    	dnalLoader = new DNALLoader();
    }
    
    public boolean loadTypeDefinition(String dnalPath) {
    	return dnalLoader.loadTypeDefinition(dnalPath);
    }
    public boolean loadTypeDefinitionFromString(String source) {
    	return dnalLoader.loadTypeDefinitionFromString(source);
    }
    
    public List<NewErrorMessage> getErrorTracker() {
    	return dnalLoader.getErrorTracker().getErrL();
    }
    
    public void dumpErrors() {
    	dnalLoader.dumpErrors();
    }
    
    public ValidationResult createFromBean(String typeName, Object bean) throws WorldException {
    	dnalLoader.beginNewDataSet();
    	DValue dval = dnalLoader.createFromBean(typeName, bean);
    	
    	if (dval != null) {
    		dnalLoader.addTopLevelValue("someName", dval);
    	}
    	
    	if (! dnalLoader.commit()) {
    		return new ValidationResult(null, dnalLoader.getErrorTracker().getErrL());
    	}
		return new ValidationResult(dval, dnalLoader.getErrorTracker().getErrL());
    }
	public void setFieldConverter(FieldConverter fieldConverter) {
		dnalLoader.setFieldConverter(fieldConverter);
	}
}