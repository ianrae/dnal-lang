package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.api.bean.BeanMethodCache;

public class BeanToDTypeBuilder {
	
	//TODO:much more complicated that this
	//make into singleton!!
	private Map<Class<?>, String> map = new HashMap<>();
	
	public BeanToDTypeBuilder() {
		map.put(int.class, "int");
		map.put(long.class, "int");
		map.put(double.class, "int");
		map.put(boolean.class, "int");
		
		
		map.put(String.class, "string");
		map.put(Boolean.class, "boolean");
		map.put(Integer.class, "int");
		map.put(Long.class, "long");
		map.put(Double.class, "number");
		map.put(Date.class, "date");
	}
	
	private String convert(Class<?> clazz) {
		return map.get(clazz);
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
	public String buildDnalView(String typeName, String viewName, BeanMethodCache methodCache1, BeanMethodCache methodCache2, List<String> xlist, List<String> dtolist) {
		//			String dnal3 = " inview X <- XDTOView { s1 <- ss1 string   s2 <- ss2 string } end";		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("inview %s <- %s {", typeName, viewName));
		for(int i = 0; i < xlist.size(); i++) {
			String fieldName = xlist.get(i);
			String dtoName = dtolist.get(i);
			String dnalTypeName = getDnalTypeName(methodCache1, fieldName);
			String dnalTypeNameDTO = getDnalTypeName(methodCache2, dtoName);
			sb.append(String.format(" %s <- %s %s", fieldName, dtoName, dnalTypeNameDTO));
		}
		sb.append(" } end");
		return sb.toString();
	}

	private String getDnalTypeName(BeanMethodCache methodCache, String fieldName) {
		Method meth = methodCache.getMethod(fieldName);
		Class<?> paramClass = meth.getReturnType();
		String dnalTypeName = convert(paramClass);
		return dnalTypeName;
	}

}