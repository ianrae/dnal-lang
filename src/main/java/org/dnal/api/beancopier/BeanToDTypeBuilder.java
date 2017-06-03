package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.api.bean.BeanMethodCache;
import org.dnal.compiler.et.XErrorTracker;

public class BeanToDTypeBuilder {

	private Map<Class<?>, String> map = new HashMap<>();
	private ListTypeFinder listTypeFinder;
	private XErrorTracker et;

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
		//list is handled separately
	}
	
	public String getPrimitive(Class<?> clazz) {
		return map.get(clazz);
	}

	public Class<?> getListElementType(Method meth, Class<?> paramClass) {
		return listTypeFinder.getListElementType(meth, paramClass);
	}
	public String generateListType(String listTypeName, String dnalTypeName) {
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
	
	public String generateStructType(FieldInfo finfo, DnalTypeDiscoverer zc) {
		//, String structTypeName, String dnalTypeName, Class<?> paramClass
		BeanMethodInvoker finder = new BeanMethodInvoker();
		BeanMethodCache methodCache = finder.getAllGetters(finfo.clazz);
		List<String> allGetters = finder.getAllFields(finfo.clazz);
		return mybuildGenDnalType(finfo.dnalTypeName, methodCache, allGetters, zc);
	}
	
	private String mybuildGenDnalType(String typeName, BeanMethodCache methodCache, List<String> xlist, DnalTypeDiscoverer zc) {
		//			String dnal = "type X struct { s1 string optional s2 string optional  } end";
		StringBuilder sb = new StringBuilder();
		sb.append("type ");
		sb.append(typeName);
		sb.append(" struct { ");
		//			String dnal = "type X struct { s1 string optional s2 string optional  } end";
		for(String fieldName: xlist) {
			Method meth = methodCache.getMethod(fieldName);
			String dnalTypeName = getGenDnalTypeName(meth, zc);
			sb.append(" ");
			sb.append(fieldName);
			sb.append(" ");
			sb.append(dnalTypeName);
			sb.append(" optional");
		}
		sb.append(" } end");
		return sb.toString();
	}
	
	private String getGenDnalTypeName(Method meth, DnalTypeDiscoverer zc) {
		Class<?> clazz = zc.getElementClassIfList(meth);
		if (clazz != null) {
			String typeName = String.format("list<%s>", zc.findAlreadyDefinedType(clazz));
			return typeName;
		} else {
			clazz = meth.getReturnType();
			return zc.findAlreadyDefinedType(clazz);
		}
	}
	
	
	

}