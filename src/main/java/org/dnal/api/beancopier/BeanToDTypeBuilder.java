package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
//		map.put(List.class, "list");
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

}