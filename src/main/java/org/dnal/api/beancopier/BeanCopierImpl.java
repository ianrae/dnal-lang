package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.api.DataSet;
import org.dnal.api.bean.BeanMethodCache;
import org.dnal.api.view.ViewLoader;
import org.dnal.core.DStructType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.logger.Log;

public class BeanCopierImpl implements BeanCopier {
	public BeanCopierContext bctx;
	private Map<Class<?>, BeanMethodCache> innerGetterCache = new HashMap<>();
	private Map<Class<?>, BeanMethodCache> innerSetterCache = new HashMap<>();

	@Override
	public boolean copy(Object sourceObj, Object destObj, List<FieldSpec> fieldL) {
		boolean ok = false;
		try {
			if (bctx == null || (! bctx.compareKeys(sourceObj, destObj, fieldL))) {
				//new params, so re-create context
				if (! prepare(sourceObj, destObj, fieldL)) {
					Log.log("BeanCopier: prepare failed");
					return false;
				}
			}
			
			ok = doCopy(sourceObj, destObj, fieldL);
			
		} catch (Exception e) {
			addError("BeanCopier Exception:" + e.getMessage());
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
			Log.log("BeanCopier: field list error");
			return false;
		}

		String destTypeName = destObj.getClass().getSimpleName();
		String sourceTypeName = sourceObj.getClass().getSimpleName();
		String viewName =  sourceTypeName + "View";

		bctx.pctB.start();
		DValue dvalSource = bctx.loader.createFromBean(viewName, sourceObj);
		if (dvalSource == null) {
			Log.log("BeanCopier: loader.createFromBean failed on sourceObj");
			return false;
		}
		bctx.pctB.end();

		bctx.pctC.start();
		DataSet ds = bctx.loader.getDataSet();
		ViewLoader viewLoader = new ViewLoader(ds);
		DValue dval = viewLoader.load(dvalSource, (DStructType) ds.getType(destTypeName));
		if (dval == null) {
			Log.log("BeanCopier: viewLoader failed ");
			return false;
		}
		bctx.pctC.end();

		//now convert dval into x
		bctx.pctD.start();
		innerGetterCache.put(destObj.getClass(), bctx.destGetterMethodCache);
		innerSetterCache.put(destObj.getClass(), bctx.destSetterMethodCache);
		ScalarConvertUtil util = new ScalarConvertUtil(bctx.loader.getErrorTracker());
		for(String fieldName: destFieldList) {
			Method meth = bctx.destSetterMethodCache.getMethod(fieldName);

			Class<?> paramClass = meth.getParameterTypes()[0];
			DValue inner = dval.asStruct().getField(fieldName);
			if (inner != null) {
				Object obj = convertToObject(util, inner, paramClass, fieldName, finder, destObj.getClass());
				if (obj == null) {
					Log.log("BeanCopier: unexpected null in convertToObject, setting destobj");
					return false;
				}
				finder.invokeSetter(bctx.destSetterMethodCache, destObj, fieldName, obj);
			}
		}
		bctx.pctD.end();
		
		if (areErrors()) {
			Log.log("BeanCopier: setting destObj failed");
			return false;
		}
		return true;
	}

	private Object convertToObject(ScalarConvertUtil util, DValue dval, Class<?> paramClass, String fieldName, BeanMethodInvoker finder, Class<?> classDest) throws Exception {
		if (dval.getType().isListShape()) {
			List<Object> list = new ArrayList<>();
			Method meth = this.getInnerGetterCache(classDest, finder).getMethod(fieldName);
			Class<?> elClass = bctx.getListElementType(meth, paramClass);
			for(DValue inner: dval.asList()) {
				if (inner != null) {
					Object obj = convertToObject(util, inner, elClass, fieldName, finder, classDest);  //recursion!
					if (obj != null) {
						list.add(obj);
					}
				}
			}
			return list;
		} else if (dval.getType().isStructShape()) {
			Method meth = bctx.destGetterMethodCache.getMethod(fieldName);
			Class<?> structClass = bctx.getListElementType(meth, paramClass);
			Object targetObj = createNewObject(structClass);
			BeanMethodCache getterMethodCache = getInnerGetterCache(structClass, finder);
			BeanMethodCache setterMethodCache = getInnerSetterCache(structClass, finder);
			
			for(String member: dval.asStruct().getFieldNames()) {
				DValue inner = dval.asStruct().getField(member);
				if (inner != null) {
					Method im = getterMethodCache.getMethod(member);
					//!!fix for lists,ect
					Class<?> imClass = im.getReturnType();
					
					Object obj = convertToObject(util, inner, imClass, member, finder, structClass);  //recursion!
					finder.invokeSetter(setterMethodCache, targetObj, member, obj);
				}
			}
			return targetObj;
		}
		return util.toObject(dval, paramClass);
	}

	private BeanMethodCache getInnerGetterCache(Class<?> structClass, BeanMethodInvoker finder) {
		BeanMethodCache methodCache = this.innerGetterCache.get(structClass);
		if (methodCache == null) {
			methodCache = finder.getAllGetters(structClass);
			innerGetterCache.put(structClass, methodCache);
		}
		return methodCache;
	}
	private BeanMethodCache getInnerSetterCache(Class<?> structClass, BeanMethodInvoker finder) {
		BeanMethodCache methodCache = this.innerSetterCache.get(structClass);
		if (methodCache == null) {
			methodCache = finder.getAllSetters(structClass);
			innerSetterCache.put(structClass, methodCache);
		}
		return methodCache;
	}


	private Object createNewObject(Class<?> elClass) {
		Object obj = null;
		try {
			obj = elClass.newInstance();
		} catch (InstantiationException e) {
			addError(String.format("InstantiationException. failed to create object '%s': %s", elClass.getName(), e.getMessage()));
		} catch (IllegalAccessException e) {
			addError(String.format("IllegalAccessException. failed to create object '%s': %s", elClass.getName(), e.getMessage()));
		}
		return obj;
	}

	private boolean areErrors() {
		return bctx.loader.getErrorTracker().areErrors();
	}

	private void addError(String message) {
		bctx.loader.getErrorTracker().addParsingError(message);
	}
	
	@Override
	public List<NewErrorMessage> getErrors() {
		List<NewErrorMessage> list = new ArrayList<>();
		if (bctx != null) {
			list = bctx.loader.getErrorTracker().getErrL();
		}
		return list;
	}
	@Override
	public void dumpErrors() {
		if (bctx != null) {
			bctx.loader.getErrorTracker().dumpErrors();
		}
	}

	@Override
	public void dumpPeformanceInfo() {
		Log.log(String.format("pctA %d", bctx.pctA.getDuration()));
		Log.log(String.format("pctB %d", bctx.pctB.getDuration()));
		Log.log(String.format("pctC %d", bctx.pctC.getDuration()));
		Log.log(String.format("pctD %d", bctx.pctD.getDuration()));
		Log.log(String.format("pctE %d", bctx.pctE.getDuration()));
	}
}