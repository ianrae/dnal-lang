package org.dnal.api.bean2;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.dnal.api.bean.BeanMethodCache;
import org.dnal.api.bean.Person;
import org.dnal.api.beancopier.BeanMethodInvoker;
import org.dnal.api.beancopier.BeanToDTypeBuilder;
import org.dnal.api.beancopier.ListTypeFinder;
import org.dnal.compiler.et.XErrorTracker;
import org.junit.Before;
import org.junit.Test;

public class NewBeanTests {
	public static class FieldInfo {
		public String name;
		public Method meth; //getter i think
	}
	
	
	public static class ZCreator {
		private ListTypeFinder listTypeFinder;
		private XErrorTracker et;
		private BeanToDTypeBuilder builder;
		private Stack<FieldInfo> stack = new Stack<>();

		public ZCreator(XErrorTracker et) {
			this.et = et;
			listTypeFinder = new ListTypeFinder(et);
			builder = new BeanToDTypeBuilder(et);
		}
		public String createForClass(Class<?> clazz, List<String> fields) {
			BeanMethodInvoker finder = new BeanMethodInvoker();
			BeanMethodCache methodCache = finder.getGetters(clazz, fields);
			
			String src = "";
			for(String fieldName: methodCache.keySet()) {
				Method method = methodCache.getMethod(fieldName);
				String classname = dotype(method);
				String s = String.format("%s:%s;", fieldName, classname);
				src += s;
			}
			return src;	
		}

		private String dotype(Method meth) {
			Class<?> clazz = meth.getReturnType();
			if (Collection.class.isAssignableFrom(clazz)) {
				clazz = listTypeFinder.getListElementType(meth, clazz);
				return "LIST:" + clazz.getSimpleName();
			}
			
			if (clazz.isEnum()) {
				String className = clazz.getName();
				return "ENUM: " + className;
			}
			
			String className = builder.getPrimitive(clazz);
			if (className != null) {
				return className;
			}
			
			//else struct
			return clazz.getName();
		}
	}

	@Test
	public void test() {
		List<String> fields = Arrays.asList("name", "age");
		String source = zc.createForClass(Person.class, fields);
		assertEquals("name:string;age:int;", source);
	}

	@Test
	public void testList() {
		List<String> fields = Arrays.asList("roles");
		String source = zc.createForClass(Person.class, fields);
		assertEquals("roles:LIST:string;", source);
	}

	@Test
	public void testListEnum() {
		List<String> fields = Arrays.asList("directions");
		String source = zc.createForClass(Person.class, fields);
		assertEquals("roles:LIST:string;", source);
	}

	//--
	private ZCreator zc;
	
	@Before 
	public void init() {
		zc = new ZCreator(null);
	}

}
