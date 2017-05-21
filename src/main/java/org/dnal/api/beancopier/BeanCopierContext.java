package org.dnal.api.beancopier;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.bean.BeanMethodCache;
import org.dnal.api.bean.DNALLoader;
import org.dnal.compiler.performance.PerfContinuingTimer;
import org.dnal.core.NewErrorMessage;

public class BeanCopierContext {
	DNALLoader loader;
	public PerfContinuingTimer pctA = new PerfContinuingTimer();
	public PerfContinuingTimer pctB = new PerfContinuingTimer();
	public PerfContinuingTimer pctC = new PerfContinuingTimer();
	public PerfContinuingTimer pctD = new PerfContinuingTimer();
	public PerfContinuingTimer pctE = new PerfContinuingTimer();

	BeanMethodCache destSetterMethodCache;
	List<String> allDestFields;
	List<String> allSourceFields;
	private BeanMethodCache destGetterMethodCache;
	private BeanMethodCache sourceGetterMethodCache;
	private BeanCopierContextKey contextKey;


	public BeanCopierContext() {
		loader = new DNALLoader();
		loader.initCompiler();
	}

	public boolean prepare(Object sourceObj, Object destObj, List<FieldSpec> fieldL) {
		this.contextKey = new BeanCopierContextKey(sourceObj, destObj, fieldL);
		try {
			if (! doPrepare(sourceObj, destObj, fieldL)) {
				return false;
			}
		} catch (Exception e) {
			addError("Exception:" + e.getMessage());
		}
		return ! areErrors();
	}

	private boolean doPrepare(Object sourceObj, Object destObj, List<FieldSpec> fieldL) throws Exception {
		pctE.start();
		BeanMethodInvoker finder = new BeanMethodInvoker();
		destSetterMethodCache = finder.getAllSetters(destObj.getClass());

		allDestFields = finder.getAllFields(destObj.getClass());
		allSourceFields = finder.getAllFields(sourceObj.getClass());
		List<String> destFieldList = new ArrayList<>();
		List<String> sourceFieldList = new ArrayList<>();
		for(FieldSpec field: fieldL) {
			if (allSourceFields.contains(field.srcField)) {
				sourceFieldList.add(field.srcField);
			} else {
				addError(String.format("src can't find field '%s'", field.srcField));
			}

			if (allDestFields.contains(field.destField)) {
				destFieldList.add(field.destField);
			} else {
				addError(String.format("dest can't find field '%s'", field.destField));
			}
		}

		if (areErrors()) {
			return false;
		}

		BeanToDTypeBuilder builder = new BeanToDTypeBuilder(loader.getErrorTracker());
		destGetterMethodCache = finder.getGetters(destObj.getClass(), destFieldList);
		sourceGetterMethodCache = finder.getGetters(sourceObj.getClass(), sourceFieldList);

		String xName = destObj.getClass().getSimpleName();
		String dtoName = sourceObj.getClass().getSimpleName();
		String viewName =  dtoName + "View";
		String dnal = builder.buildDnalType(xName, destGetterMethodCache, destFieldList);
		String dnal2 = builder.buildDnalType(dtoName, sourceGetterMethodCache, sourceFieldList);
		String dnal3 = builder.buildDnalView(xName, viewName, destGetterMethodCache, sourceGetterMethodCache, destFieldList, sourceFieldList);

		//			XErrorTracker.logErrors = true;
		//			Log.debugLogging = true;
		boolean b = loader.loadTypeDefinitionFromString(String.format("%s %s %s", dnal, dnal2, dnal3));
		if (! b) {
			return false;
		}
		pctE.end();

		return true;
	}

	private boolean areErrors() {
		return loader.getErrorTracker().areErrors();
	}

	private void addError(String message) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.PARSING_ERROR);
		nem.setMessage(message);
		loader.getErrorTracker().addError(nem);
	}

	public boolean compareKeys(Object dto, Object x, List<FieldSpec> fieldL) {
		BeanCopierContextKey newKey = new BeanCopierContextKey(dto, x, fieldL);
		if (contextKey.equals(newKey)) {
			return true;
		} else {
			return false;
		}
	}

	public void clearErrors() {
		loader.getErrorTracker().clear();
	}
}