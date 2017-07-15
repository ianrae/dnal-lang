package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.dnal.compiler.et.XErrorTracker;

public class ListTypeFinder {
	private XErrorTracker et;
	public int listDepth = 0;

	public ListTypeFinder(XErrorTracker et) {
		this.et = et;
	}
	
	public boolean isListType(Class<?>paramClass) {
		listDepth = 0;
		if (Collection.class.isAssignableFrom(paramClass)) {
			return true;
		}
		return false;
	}

	public Class<?> getListElementType(Method meth, Class<?> paramClass) {
		listDepth = 0;
		Type returnType = meth.getGenericReturnType();
		Class<?> clazz = handleType(returnType, paramClass);
		return clazz;
	}

	//handles list<list<>> but not list<set<>>. TODO:fix later!!
	private Class<?> handleType(Type returnType, Class<?> paramClass) {
		if (returnType instanceof ParameterizedType) {
			listDepth++;
			ParameterizedType paramType = (ParameterizedType) returnType;
			Type[] argTypes = paramType.getActualTypeArguments();
			if (argTypes.length > 0) {
				Type type = argTypes[0];
				if (type instanceof Class) {
					@SuppressWarnings("unchecked")
					Class<?> inner = (Class<?>) type;
					return inner;
				} else if (type instanceof ParameterizedType) {
					return handleType(type, paramClass);
				} else {
					et.addParsingError(String.format("generiuc list element type is unsupported type '%s'", paramClass.getSimpleName()));
				}
			}
		} else if (returnType instanceof Class) {
			return (Class<?>) returnType;
		}
		return null;
	}
	
}