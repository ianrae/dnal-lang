package org.dnal.api.bean;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dnal.core.DStructType;
import org.junit.Test;


public class BeanCopyTests {
	
	public static class BeanMethodFinder {
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
	
	@Test
	public void test() {
		BeanMethodFinder finder = new BeanMethodFinder();
		BeanMethodCache methodCache = finder.getAllGetters(ReflectionBeanLoaderTest.ClassA.class);
		assertEquals(6, methodCache.size());
		assertNotNull(methodCache.getMethod("nval"));
	}
	@Test
	public void testFilter() {
		BeanMethodFinder finder = new BeanMethodFinder();
		List<String> filter = Collections.singletonList("nval");
		BeanMethodCache methodCache = finder.getGetters(ReflectionBeanLoaderTest.ClassA.class, filter);
		assertEquals(1, methodCache.size());
		assertNotNull(methodCache.getMethod("nval"));
	}
	
	
	
	private void log(String s) {
		System.out.println(s);;
	}

}
