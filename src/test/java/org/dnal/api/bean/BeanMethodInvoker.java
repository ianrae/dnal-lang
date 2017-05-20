package org.dnal.api.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BeanMethodInvoker {
	public List<String> getAllFields(Class<?> beanClass) {
		BeanMethodCache methodCache = getAllGetters(beanClass);
		List<String> resultL = new ArrayList<>();
		for(String fieldName: methodCache.keySet()) {
			resultL.add(fieldName);
		}
		return resultL;
	}
	
	
	public BeanMethodCache getAllGetters(Class<?> beanClass) {
		return doGetAllGetters(beanClass, null);
	}
	public BeanMethodCache getGetters(Class<?> beanClass, List<String> filter) {
		return doGetAllGetters(beanClass, filter);
	}

	private BeanMethodCache doGetAllGetters(Class<?> beanClass, List<String> filter) {
		BeanMethodCache methodCache = new BeanMethodCache();
		Method[] ar = beanClass.getMethods();
		for(Method method: ar) {
			String mname = method.getName();
			if (method.getParameterCount() == 0) {
				if (mname.equals("getClass")) {
					continue;
				}

				if (mname.startsWith("get")) {
					String name = mname.substring(3); //remove get
					name = lowify(name);
					if (wantMethod(name, filter)) {
						methodCache.add(name, method);
					}
				} else if (mname.startsWith("is")) {
					String name = mname.substring(2); //remove is
					name = lowify(name);
					if (wantMethod(name, filter)) {
						methodCache.add(name, method);
					}
				}
			}
		}
		return methodCache;
	}

	public Object invokeGetter(BeanMethodCache methodCache, Object bean, String fieldName) throws Exception {
		Method meth = methodCache.getMethod(fieldName);
		if (meth == null) {
			throw new IllegalArgumentException(String.format("missing method for: '%s'", fieldName));
		}

		Object res = null;
		res = meth.invoke(bean);
		return res;
	}

	//--setters
	public BeanMethodCache getAllSetters(Class<?> beanClass) {
		return doGetAllSetters(beanClass, null);
	}
	public BeanMethodCache getSetters(Class<?> beanClass, List<String> filter) {
		return doGetAllSetters(beanClass, filter);
	}

	private BeanMethodCache doGetAllSetters(Class<?> beanClass, List<String> filter) {
		BeanMethodCache methodCache = new BeanMethodCache();
		Method[] ar = beanClass.getMethods();
		for(Method method: ar) {
			String mname = method.getName();
			if (method.getParameterCount() == 1) {

				if (mname.startsWith("set")) {
					String name = mname.substring(3); //remove set
					name = lowify(name);
					if (wantMethod(name, filter)) {
						methodCache.add(name, method);
					}
				}
			}
		}
		return methodCache;
	}

	public void invokeSetter(BeanMethodCache methodCache, Object bean, String fieldName, Object value) throws Exception {
		Method meth = methodCache.getMethod(fieldName);
		if (meth == null) {
			throw new IllegalArgumentException(String.format("missing method for: '%s'", fieldName));
		}

		meth.invoke(bean, value);
	}


	private boolean wantMethod(String name, List<String> filter) {
		if (filter == null) {
			return true;
		}
		return filter.contains(name);
	}

	private String lowify(String s) {
		if (s.isEmpty()) {
			return s;
		} else {
			String s1 = s.substring(0, 1).toLowerCase() + s.substring(1);
			return s1;
		}
	}
}