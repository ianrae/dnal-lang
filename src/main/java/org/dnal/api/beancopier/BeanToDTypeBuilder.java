package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dnal.api.bean.BeanMethodCache;
import org.dnal.compiler.et.XErrorTracker;

public class BeanToDTypeBuilder {

	//TODO:much more complicated that this
	//make into singleton!!
	private Map<Class<?>, String> map = new HashMap<>();
	private XErrorTracker et;
	private Map<String, String> listTypeMap = new HashMap<>();
	private Map<String, String> structTypeMap = new HashMap<>();
	private ListTypeFinder listTypeFinder;

	public BeanToDTypeBuilder(XErrorTracker et) {
		this.et = et;
		listTypeFinder = new ListTypeFinder(et);
		
		map.put(int.class, "int");
		map.put(byte.class, "int");
		map.put(short.class, "int");
		map.put(long.class, "long");
		map.put(double.class, "number");
		map.put(float.class, "number");
		map.put(boolean.class, "boolean");

		map.put(String.class, "string");
		map.put(Boolean.class, "boolean");
		map.put(Integer.class, "int");
		map.put(Byte.class, "int");
		map.put(Short.class, "int");
		map.put(Long.class, "long");
		map.put(Double.class, "number");
		map.put(Float.class, "number");
		map.put(Date.class, "date");
		map.put(List.class, "list");
	}
	
	public String getPrimitive(Class<?> clazz) {
		return map.get(clazz);
	}

//	private String convert(Class<?> clazz) {
//		return convert(clazz, false);
//	}
	private String convert(Class<?> clazz, boolean allowMissing) {
		String dnalType = map.get(clazz);
		
		if (dnalType == null) {
			if (clazz.isEnum()) {
				return clazz.getSimpleName();
			}

			if (! allowMissing) {
				et.addParsingError(String.format("unsupported type '%s'", clazz.getSimpleName()));
			} else {
				return "STRUCT:" + clazz.getSimpleName();
			}
		}
		return dnalType;
	}

	public String buildDnalType(String typeName, BeanMethodCache methodCache, List<String> xlist) {
		//			String dnal = "type X struct { s1 string optional s2 string optional  } end";
		StringBuilder sb = new StringBuilder();
		sb.append("type ");
		sb.append(typeName);
		sb.append(" struct { ");
		//			String dnal = "type X struct { s1 string optional s2 string optional  } end";
		for(String fieldName: xlist) {
			String dnalTypeName = getDnalTypeName(methodCache, fieldName);
			sb.append(" ");
			sb.append(fieldName);
			sb.append(" ");
			sb.append(dnalTypeName);
			sb.append(" optional");
		}
		sb.append(" } end");
		return sb.toString();
	}
	public String buildDnalView(String typeName, String viewName, BeanMethodCache methodCache1, BeanMethodCache methodCache2, List<String> xlist, List<String> dtolist, List<FieldSpec> fieldL) {
		//			String dnal3 = " inview X <- XDTOView { s1 <- ss1 string   s2 <- ss2 string } end";		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("inview %s <- %s {", typeName, viewName));

		for(FieldSpec spec: fieldL) {
			String fieldName = spec.destField;
			String dtoName = spec.srcField;
			String dnalTypeName = getDnalTypeName(methodCache1, fieldName);
			String dnalTypeNameDTO = getDnalTypeName(methodCache2, dtoName);
			sb.append(String.format(" %s <- %s %s", fieldName, dtoName, dnalTypeNameDTO));
		}
		sb.append(" } end");
		return sb.toString();
	}

	private String getDnalTypeName(BeanMethodCache methodCache, String fieldName) {
		return getDnalTypeNameRaw(methodCache, fieldName, false);
	}
	private String getDnalTypeNameRaw(BeanMethodCache methodCache, String fieldName, boolean allowMissing) {
		Method meth = methodCache.getMethod(fieldName);
		Class<?> paramClass = meth.getReturnType();
		if (structTypeMap.containsKey(paramClass.getSimpleName())) {
			return structTypeMap.get(paramClass.getSimpleName());
		}
		
		if (listTypeFinder.isListType(paramClass)) {
			return calculateListType(meth, paramClass, allowMissing);
		}
		String dnalTypeName = convert(paramClass, allowMissing);
		return dnalTypeName;
	}

	private String calculateListType(Method meth, Class<?> paramClass, boolean allowMissing) {
		Class<?> inner = listTypeFinder.getListElementType(meth, paramClass);
		if (inner != null) {
			String elType = convert(inner, allowMissing);
			
			String s = "";
			switch(listTypeFinder.listDepth) {
			case 1:
				s =  String.format("list<%s>", elType);
				break;
			case 2:
				s =  String.format("list<list<%s>>", elType);
				break;
			case 3:
				s =  String.format("list<list<list<%s>>>", elType);
				break;
			default:
				break;
			}
			
			if (listTypeMap.containsKey(s)) {
				return listTypeMap.get(s);
			}
			return s;
		}
		return null;
	}
	public Class<?> getListElementType(Method meth, Class<?> paramClass) {
		return listTypeFinder.getListElementType(meth, paramClass);
	}


	public String buildEnums(BeanMethodCache sourceGetterMethodCache, BeanMethodCache destGetterMethodCache) {
		Map<Class<?>, String> seenAlreadyMap = new HashMap<>();
		String dnal = "";

		for(String fieldName : sourceGetterMethodCache.keySet()) {
			dnal += doEnum(sourceGetterMethodCache, fieldName, seenAlreadyMap);
		}

		for(String fieldName : destGetterMethodCache.keySet()) {
			dnal += doEnum(destGetterMethodCache, fieldName, seenAlreadyMap);
		}
		return dnal;
	}
	private String doEnum(BeanMethodCache methodCache, String fieldName, Map<Class<?>, String> seenAlreadyMap) {
		Method meth = methodCache.getMethod(fieldName);
		Class<?> clazz = meth.getReturnType();
		if (Collection.class.isAssignableFrom(clazz)) {
			clazz = listTypeFinder.getListElementType(meth, clazz);
		}
		
		String dnal = "";
		if (clazz.isEnum()) {
			String className = clazz.getName();
			if (!seenAlreadyMap.containsKey(clazz)) {
				dnal += generateEnum(clazz);
				seenAlreadyMap.put(clazz, "");
			}
		}
		return dnal;
	}
	
	public String buildListTypes(BeanMethodCache sourceGetterMethodCache, BeanMethodCache destGetterMethodCache) {
		String dnal = "";

		int nextInstanceId = 100;
		for(String fieldName : sourceGetterMethodCache.keySet()) {
			String dnalTypeName = getDnalTypeName(sourceGetterMethodCache, fieldName);
			if (dnalTypeName != null && dnalTypeName.startsWith("list<")) {
				if (!listTypeMap.containsKey(dnalTypeName)) {
					String listTypeName = generateListTypeName(nextInstanceId++);
					dnal += generateListType(listTypeName, dnalTypeName);
					listTypeMap.put(dnalTypeName, listTypeName);
				}
			}
		}

		for(String fieldName : destGetterMethodCache.keySet()) {
			String dnalTypeName = getDnalTypeName(destGetterMethodCache, fieldName);
			if (dnalTypeName != null && dnalTypeName.startsWith("list<")) {
				if (!listTypeMap.containsKey(dnalTypeName)) {
					String listTypeName = generateListTypeName(nextInstanceId++);
					dnal += generateListType(listTypeName, dnalTypeName);
					listTypeMap.put(dnalTypeName, listTypeName);
				}
			}
		}
		return dnal;
	}

	private String generateListTypeName(int nextInstanceId) {
		String s = String.format("%s%d", "List", nextInstanceId);
		return s;
	}
	private String generateListType(String listTypeName, String dnalTypeName) {
		String s = String.format(" type %s %s end", listTypeName, dnalTypeName);
		return s;
	}

	public String generateEnum(Class<?> paramClass) {

		StringBuilder sb = new StringBuilder();
		sb.append(" type ");
		sb.append(paramClass.getSimpleName());
		sb.append(" enum { ");

		for(int i = 0; i < paramClass.getEnumConstants().length; i++) {
			Object x = paramClass.getEnumConstants()[i];
			if (i > 0) {
				sb.append(" ");
			}
			sb.append(x.toString());
		}
		sb.append(" } end ");
		return sb.toString();
	}
	
	public String buildStructTypes(BeanMethodCache sourceGetterMethodCache, BeanMethodCache destGetterMethodCache) {
		String dnal = "";

		int nextInstanceId = 100;
		for(String fieldName : sourceGetterMethodCache.keySet()) {
			dnal += doStruct(sourceGetterMethodCache, fieldName, nextInstanceId++);
			dnal += "\n";
		}

		for(String fieldName : destGetterMethodCache.keySet()) {
			dnal += doStruct(destGetterMethodCache, fieldName, nextInstanceId++);
		}
		return dnal;
	}
	
	private String doStruct(BeanMethodCache methodCache, String fieldName, int nextInstanceId) {
		String dnal = "";
		String target = "STRUCT:";

		String dnalTypeName = getDnalTypeNameRaw(methodCache, fieldName, true);
		if (dnalTypeName.startsWith(target)) {
			dnalTypeName = StringUtils.substringAfter(dnalTypeName, target);
			if (!structTypeMap.containsKey(dnalTypeName)) {
				
				Method meth = methodCache.getMethod(fieldName);
				Class<?> paramClass = meth.getReturnType();
				String structTypeName = String.format("%s%d", "Struct", nextInstanceId++);
				dnal += generateStructType(structTypeName, dnalTypeName, paramClass);
				structTypeMap.put(dnalTypeName, structTypeName);
			}
		}
		return dnal;
	}

	private String generateStructType(String structTypeName, String dnalTypeName, Class<?> paramClass) {
		BeanMethodInvoker finder = new BeanMethodInvoker();
		BeanMethodCache methodCache = finder.getAllGetters(paramClass);
		List<String> allGetters = finder.getAllFields(paramClass);
		return buildDnalType(structTypeName, methodCache, allGetters);
	}
	

}