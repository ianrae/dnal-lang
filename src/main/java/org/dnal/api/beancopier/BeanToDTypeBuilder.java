package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.dnal.api.bean.BeanMethodCache;

public class BeanToDTypeBuilder {

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


	//TODO:much more complicated that this
	private String convert(Class<?> clazz) {
		if (clazz.equals(String.class)) {
			return "string";
		} else if (clazz.equals(Boolean.class)) {
			return "boolean";
		} else if (clazz.equals(Integer.class)) {
			return "int";
		} else if (clazz.equals(Long.class)) {
			return "long";
		} else if (clazz.equals(Double.class)) {
			return "number";
		} else if (clazz.equals(Date.class)) {
			return "date";
		} else {
			return null;
		}
	}
}