package org.dnal.api.bean2;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
		public Class<?> clazz;
		public String fieldName;
		public Method meth; //getter i think
		public String dnalTypeName;
		public boolean isEnum;
		
		public FieldInfo(Class<?> clazz, String name) {
			this.clazz = clazz;
			this.fieldName = name;
		}
	}
	
	
	public static class ZCreator {
		private ListTypeFinder listTypeFinder;
		private XErrorTracker et;
		private BeanToDTypeBuilder builder;
		private Stack<FieldInfo> stack = new Stack<>();
		private List<FieldInfo> genList = new ArrayList<>();
		private BeanMethodInvoker finder = new BeanMethodInvoker();

		public ZCreator(XErrorTracker et) {
			this.et = et;
			listTypeFinder = new ListTypeFinder(et);
			builder = new BeanToDTypeBuilder(et);
		}
		public String createForClass(Class<?> clazz, List<String> fields) {
			for(String fieldName: fields) {
				FieldInfo finfo = new FieldInfo(clazz, fieldName);
				stack.push(finfo);
			}
			
			int retries = 0;
			while(! stack.isEmpty())  {
				if (retries > 10) {
					et.addParsingError("retry runaway!");
					break; //!!error
				}
				
				retries++;
				FieldInfo finfo = stack.peek();
				if (resolve(finfo)) {
					genList.add(finfo);
					stack.pop();
					retries = 0;
				}
			}
			
			
			
			String src = "";
			for(FieldInfo fino: genList) {
				String s = String.format("%s:%s;", fino.fieldName, fino.dnalTypeName);
				src += s;
			}
			return src;	
		}

		private boolean resolve(FieldInfo finfo) {
			BeanMethodCache methodCache = finder.getGetters(finfo.clazz, Collections.singletonList(finfo.fieldName));
			
			//should be only one
			for(String fieldName: methodCache.keySet()) {
				finfo.meth = methodCache.getMethod(fieldName);
				boolean b = determineClass(finfo);
				return b;
			}
			
			et.addParsingError("bad " + finfo.fieldName);
			return true; //!!error
		}
		
		
		private boolean determineClass(FieldInfo finfo) {
			Class<?> clazz = finfo.meth.getReturnType();
			if (Collection.class.isAssignableFrom(clazz)) {
				clazz = listTypeFinder.getListElementType(finfo.meth, clazz);
				FieldInfo newInfo = new FieldInfo(clazz, finfo.fieldName);
				newInfo.dnalTypeName = "list<" +clazz.getSimpleName() + ">";
				stack.push(newInfo);
				return false;
			}
			
			if (clazz.isEnum()) {
				FieldInfo newInfo = new FieldInfo(clazz, finfo.fieldName);
				newInfo.isEnum = true;
				newInfo.dnalTypeName = clazz.getSimpleName();
				genList.add(newInfo);
				return true;
			}
			
			String className = builder.getPrimitive(clazz);
			if (className != null) {
				finfo.dnalTypeName = className;
				return true;
			}
			
			//else struct
			FieldInfo newInfo = new FieldInfo(clazz, finfo.fieldName);
			stack.push(newInfo);
			return false;
		}
	}

	@Test
	public void test() {
		List<String> fields = Arrays.asList("name", "age");
		String source = zc.createForClass(Person.class, fields);
		assertEquals("age:int;name:string;", source);
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
