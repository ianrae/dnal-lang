package org.dnal.api.bean2;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.dnal.api.bean.BeanMethodCache;
import org.dnal.api.bean.DNALLoader;
import org.dnal.api.beancopier.BeanCopierContextKey;
import org.dnal.api.beancopier.BeanMethodInvoker;
import org.dnal.api.beancopier.BeanToDTypeBuilder;
import org.dnal.api.beancopier.FieldSpec;
import org.dnal.compiler.performance.PerfContinuingTimer;
import org.dnal.core.logger.Log;

public class ZBeanCopierContext {
	DNALLoader loader;
	public PerfContinuingTimer pctA = new PerfContinuingTimer();
	public PerfContinuingTimer pctB = new PerfContinuingTimer();
	public PerfContinuingTimer pctC = new PerfContinuingTimer();
	public PerfContinuingTimer pctD = new PerfContinuingTimer();
	public PerfContinuingTimer pctE = new PerfContinuingTimer();

	BeanMethodCache destSetterMethodCache;
	List<String> allDestFields;
	List<String> allSourceFields;
	BeanMethodCache destGetterMethodCache;
	private BeanMethodCache sourceGetterMethodCache;
	private BeanCopierContextKey contextKey;
	private BeanToDTypeBuilder builder;
	private ZCreator zc;

	public ZBeanCopierContext() {
		loader = new DNALLoader();
		loader.initCompiler();
		builder = new BeanToDTypeBuilder(loader.getErrorTracker());
		zc = new ZCreator(loader.getErrorTracker());
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
		//only the needed fields, and avoid duplicates
		for(FieldSpec field: fieldL) {
			if (allSourceFields.contains(field.srcField)) {
				if (! sourceFieldList.contains(field.srcField)) {
					sourceFieldList.add(field.srcField);
				}
			} else {
				addError(String.format("src can't find field '%s'", field.srcField));
			}

			if (allDestFields.contains(field.destField)) {
				if (! destFieldList.contains(field.destField)) {
					destFieldList.add(field.destField);
				}
			} else {
				addError(String.format("dest can't find field '%s'", field.destField));
			}
		}

		if (areErrors()) {
			return false;
		}

		destGetterMethodCache = finder.getGetters(destObj.getClass(), destFieldList);
		sourceGetterMethodCache = finder.getGetters(sourceObj.getClass(), sourceFieldList);

		String xName = destObj.getClass().getSimpleName();
		String dtoName = sourceObj.getClass().getSimpleName();
		String viewName =  dtoName + "View";
		String dnalGen = buildGeneratedTypes(sourceObj.getClass(), sourceFieldList, destObj.getClass(), destFieldList);
		String dnal = mybuildDnalType(xName, destFieldList, false);
		String dnal2 = ""; //builder.buildDnalType(dtoName, sourceGetterMethodCache, sourceFieldList);
		String dnal3 = mybuildDnalView(xName, viewName, destGetterMethodCache, sourceGetterMethodCache, destFieldList, sourceFieldList, fieldL);

		if (areErrors()) {
			return false;
		}
		
		//			XErrorTracker.logErrors = true;
		//			Log.debugLogging = true;
		String fullSource = String.format("%s\n %s\n %s\n %s\n", dnalGen, dnal, dnal2, dnal3);
		Log.log("fullsrc:" + fullSource);
		boolean b = loader.loadTypeDefinitionFromString(fullSource);
		if (! b) {
			Log.log("FAILED: " + fullSource); 
			return false;
		}
		pctE.end();

		return true;
	}
	
	private String buildGeneratedTypes(Class<?> srcClass, List<String> sourceFieldList, Class<?> destClass, List<String> destFieldList) {
		boolean b = zc.createForClass(srcClass, sourceFieldList, true);
		if (! b) {
			return "";
		}
		
		b = zc.createForClass(destClass, destFieldList, false);
		if (! b) {
			return "";
		}
		
		String s = "";
		String lf = System.getProperty("line.separator");
		List<FieldInfo> infos = zc.getGenList();
		for(FieldInfo finfo: infos) {
			if (finfo.isEnum) {
				s += builder.generateEnum(finfo.clazz) + lf;
			} else if (finfo.isList) {
				s += builder.generateListType(finfo.fieldName, finfo.dnalTypeName) + lf;
			} else {
				s += generateStructType(finfo) + lf;
			}
		}
		
		return s;
	}
	
	private String generateStructType(FieldInfo finfo) {
		//, String structTypeName, String dnalTypeName, Class<?> paramClass
		BeanMethodInvoker finder = new BeanMethodInvoker();
		BeanMethodCache methodCache = finder.getAllGetters(finfo.clazz);
		List<String> allGetters = finder.getAllFields(finfo.clazz);
		return mybuildGenDnalType(finfo.dnalTypeName, methodCache, allGetters);
	}
	
	private String mybuildGenDnalType(String typeName, BeanMethodCache methodCache, List<String> xlist) {
		//			String dnal = "type X struct { s1 string optional s2 string optional  } end";
		StringBuilder sb = new StringBuilder();
		sb.append("type ");
		sb.append(typeName);
		sb.append(" struct { ");
		//			String dnal = "type X struct { s1 string optional s2 string optional  } end";
		for(String fieldName: xlist) {
			Method meth = methodCache.getMethod(fieldName);
			String dnalTypeName = getGenDnalTypeName(meth);
			sb.append(" ");
			sb.append(fieldName);
			sb.append(" ");
			sb.append(dnalTypeName);
			sb.append(" optional");
		}
		sb.append(" } end");
		return sb.toString();
	}
	
	private String mybuildDnalType(String typeName, List<String> xlist, boolean isSourceClass) {
		//			String dnal = "type X struct { s1 string optional s2 string optional  } end";
		StringBuilder sb = new StringBuilder();
		sb.append("type ");
		sb.append(typeName);
		sb.append(" struct { ");
		//			String dnal = "type X struct { s1 string optional s2 string optional  } end";
		for(String fieldName: xlist) {
			String dnalTypeName = getOutputFieldDnalTypeName(fieldName, isSourceClass);
			sb.append(" ");
			sb.append(fieldName);
			sb.append(" ");
			sb.append(dnalTypeName);
			sb.append(" optional");
		}
		sb.append(" } end");
		return sb.toString();
	}

	private String getOutputFieldDnalTypeName(String fieldName, boolean isSourceClass) {
		for(FieldInfo finfo: zc.getOutputFieldList()) {
			if (finfo.isSourceClass == isSourceClass && finfo.fieldName.equals(fieldName)) {
				return finfo.dnalTypeName;
			}
		}
		return null;
	}
	private String getGenDnalTypeName(Method meth) {
		Class<?> clazz = zc.getElementClassIfList(meth);
		if (clazz == null) {
			clazz = meth.getReturnType();
			String typeName = String.format("list<%s>", zc.findAlreadyDefinedType(clazz));
			return typeName;
		} else {
			return zc.findAlreadyDefinedType(clazz);
		}
	}
	
	private String mybuildDnalView(String typeName, String viewName, BeanMethodCache methodCache1, BeanMethodCache methodCache2, List<String> xlist, List<String> dtolist, List<FieldSpec> fieldL) {
		//			String dnal3 = " inview X <- XDTOView { s1 <- ss1 string   s2 <- ss2 string } end";		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("inview %s <- %s {", typeName, viewName));

		for(FieldSpec spec: fieldL) {
			String fieldName = spec.destField;
			String dtoName = spec.srcField;
			String dnalTypeName = getOutputFieldDnalTypeName(fieldName, false);
			String dnalTypeNameDTO = getOutputFieldDnalTypeName(dtoName, true);
			sb.append(String.format(" %s <- %s %s", fieldName, dtoName, dnalTypeNameDTO));
		}
		sb.append(" } end");
		return sb.toString();
	}
	

	public Class<?> getListElementType(Method meth, Class<?> paramClass) {
		return builder.getListElementType(meth, paramClass);
	}

	private boolean areErrors() {
		return loader.getErrorTracker().areErrors();
	}

	private void addError(String message) {
		loader.getErrorTracker().addParsingError(message);
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