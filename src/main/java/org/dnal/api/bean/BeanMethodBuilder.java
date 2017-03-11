package org.dnal.api.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.ErrorType;
import org.dnal.core.TypePair;

public class BeanMethodBuilder  {

	private Map<Class<?>,BeanMethodCache> methodCacheMap = new HashMap<>();
    private XErrorTracker et;

	public BeanMethodBuilder(XErrorTracker et) {
		this.et = et;
	}

	
	private void addError(String msg) {
		NewErrorMessage errMsg = new NewErrorMessage();
		errMsg.setErrorName(ErrorType.WRONGTYPE.name());
		errMsg.setMessage(msg);
		et.addError(errMsg);
	}

	public void buildMethodCacheIfNeeded(Object bean, DStructType dtype) {
		if (methodCacheMap.containsKey(bean.getClass())) {
			return;
		}
		BeanMethodCache methodCache = enumMethods(bean, dtype);

		List<TypePair> allFields = dtype.getAllFields();
		for(TypePair pair: allFields) {
			Method meth = methodCache.getMethod(pair.name);
			if (meth == null) {
				addError(String.format("bean class '%s': can't find getter method for: %s", bean.getClass().getName(), pair.name));
			}
		}
		methodCacheMap.put(bean.getClass(), methodCache);
	}
	private BeanMethodCache enumMethods(Object bean, DStructType dtype)  {
		BeanMethodCache methodCache = new BeanMethodCache();
		Method[] ar = bean.getClass().getMethods();
		for(Method method: ar) {
			String mname = method.getName();
			if (method.getParameterCount() == 0) {
				if (mname.equals("getClass")) {
					continue;
				}

				if (mname.startsWith("get")) {
					String name = mname.substring(3); //remove get
					name = lowify(name);
					if (dtype.getFields().containsKey(name)) {
						methodCache.add(name, method);
					}
				} else if (mname.startsWith("is")) {
					String name = mname.substring(2); //remove is
					name = lowify(name);
					if (dtype.getFields().containsKey(name)) {
						methodCache.add(name, method);
					}
				}
			}
		}
		return methodCache;
	}

	private String lowify(String s) {
		if (s.isEmpty()) {
			return s;
		} else {
			String s1 = s.substring(0, 1).toLowerCase() + s.substring(1);
			return s1;
		}
	}

	public Object getFromBean(Object bean, String fieldName, DType type)  {
		BeanMethodCache methodCache = this.methodCacheMap.get(bean.getClass());
		Method meth = methodCache.getMethod(fieldName);
		if (meth == null) {
//			addError("missing method for: " + fieldName);
			return null;
		}

		Object res = null;
		try {
			res = meth.invoke(bean);
		} catch (IllegalAccessException e) {
			addError("method-invoke exception: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			addError("method-invoke exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			addError("method-invoke exception: " + e.getMessage());
		}
		return res;
	}

}