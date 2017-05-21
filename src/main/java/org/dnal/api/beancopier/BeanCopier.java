package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.dnal.api.DataSet;
import org.dnal.api.view.ViewLoader;
import org.dnal.core.DStructType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;

public class BeanCopier {
	public BeanCopierContext bctx;

	public boolean copy(Object sourceObj, Object destObj, List<FieldSpec> fieldL) {
		boolean ok = false;
		try {
			if (bctx == null || (! bctx.compareKeys(sourceObj, destObj, fieldL))) {
				//new params, so re-create context
				if (! prepare(sourceObj, destObj, fieldL)) {
					return false;
				}
			}
			
			ok = doCopy(sourceObj, destObj, fieldL);
			
		} catch (Exception e) {
			addError("Exception:" + e.getMessage());
		}
		return ok;
	}

	private boolean prepare(Object sourceObj, Object destObj, List<FieldSpec> fieldL) throws Exception {
		bctx = new BeanCopierContext();
		return  bctx.prepare(sourceObj, destObj, fieldL);
	}

	private boolean doCopy(Object sourceObj, Object destObj, List<FieldSpec> fieldL) throws Exception {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		bctx.clearErrors();
		
		bctx.pctA.start();
		List<String> destFieldList = new ArrayList<>();
		List<String> sourceFieldList = new ArrayList<>();
		for(FieldSpec field: fieldL) {
			if (bctx.allSourceFields.contains(field.srcField)) {
				sourceFieldList.add(field.srcField);
			} else {
				addError(String.format("src can't find field '%s'", field.srcField));
			}

			if (bctx.allDestFields.contains(field.destField)) {
				destFieldList.add(field.destField);
			} else {
				addError(String.format("dest can't find field '%s'", field.destField));
			}
		}
		bctx.pctA.end();

		if (areErrors()) {
			return false;
		}

		String destTypeName = destObj.getClass().getSimpleName();
		String sourceTypeName = sourceObj.getClass().getSimpleName();
		String viewName =  sourceTypeName + "View";

		bctx.pctB.start();
		DValue dvalSource = bctx.loader.createFromBean(viewName, sourceObj);
		if (dvalSource == null) {
			return false;
		}
		bctx.pctB.end();

		bctx.pctC.start();
		DataSet ds = bctx.loader.getDataSet();
		ViewLoader viewLoader = new ViewLoader(ds);
		DValue dval = viewLoader.load(dvalSource, (DStructType) ds.getType(destTypeName));
		if (dval == null) {
			return false;
		}
		bctx.pctC.end();

		//now convert dval into x
		bctx.pctD.start();
		ScalarConvertUtil util = new ScalarConvertUtil();
		for(String fieldName: destFieldList) {
			Method meth = bctx.destSetterMethodCache.getMethod(fieldName);

			Class<?> paramClass = meth.getParameterTypes()[0];
			DValue inner = dval.asStruct().getField(fieldName);
			if (inner != null) {
				Object obj = util.toObject(inner, paramClass);
				if (obj == null) {
					return false;
				}
				finder.invokeSetter(bctx.destSetterMethodCache, destObj, fieldName, obj);
			}
		}
		bctx.pctD.end();
		return true;
	}

	private boolean areErrors() {
		return bctx.loader.getErrorTracker().areErrors();
	}

	private void addError(String message) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.PARSING_ERROR);
		nem.setMessage(message);
		bctx.loader.getErrorTracker().addError(nem);
	}
}