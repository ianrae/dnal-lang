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
		public boolean isList;
		public boolean needsType;
		
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
		private int nextListNameId = 1;

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
//					genList.add(finfo);
					stack.pop();
					retries = 0;
				}
			}
			
			String src = "";
			for(FieldInfo fino: genList) {
				if (fino.isEnum) {
					String s = String.format("ENUM %s:%s;", fino.fieldName, fino.dnalTypeName);
					src += s;
				} else if (fino.isList) {
					String s = String.format("LIST %s:%s;", fino.fieldName, fino.dnalTypeName);
					src += s;
				} else {
					String s = String.format("%s:%s;", fino.fieldName, fino.dnalTypeName);
					src += s;
				}
			}
			return src;	
		}

		private boolean resolve(FieldInfo finfo) {
			if (finfo.needsType) {
				return determineClass(finfo, finfo.clazz);
			}
			
			BeanMethodCache methodCache = finder.getGetters(finfo.clazz, Collections.singletonList(finfo.fieldName));
			
			//should be only one
			for(String fieldName: methodCache.keySet()) {
				finfo.meth = methodCache.getMethod(fieldName);
				Class<?> fieldClass = finfo.meth.getReturnType();
				boolean b = determineClass(finfo, fieldClass);
				return b;
			}
			
			et.addParsingError("bad " + finfo.fieldName);
			return true; //!!error
		}
		
		
		private boolean determineClass(FieldInfo finfo, Class<?> clazz) {
			if (Collection.class.isAssignableFrom(clazz)) {
				Class<?> elementClazz = listTypeFinder.getListElementType(finfo.meth, clazz);
				if (! alreadyDefined(elementClazz)) {
					FieldInfo newInfo = new FieldInfo(elementClazz, elementClazz.getSimpleName());
					newInfo.needsType = true;
					stack.push(newInfo);
					return false;
				}
				
				finfo.dnalTypeName = String.format("List%d", nextListNameId++);
				
				FieldInfo newInfo = new FieldInfo(clazz, finfo.dnalTypeName);
				newInfo.isList = true;
				newInfo.dnalTypeName = calculateListType(elementClazz);
				genList.add(newInfo);
				genList.add(finfo);
				return true;
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
				genList.add(finfo);
				return true;
			}
			
			//else struct
			FieldInfo newInfo = new FieldInfo(clazz, finfo.fieldName);
			stack.push(newInfo);
			return false;
		}
		
		private boolean alreadyDefined(Class<?> clazz) {
			String className = builder.getPrimitive(clazz);
			if (className != null) {
				return true;
			}
			
			String target = clazz.getSimpleName();
			for(FieldInfo finfo: genList) {
				if (target.equals(finfo.dnalTypeName)) {
					return true;
				}
			}
			return false;
		}
		private String calculateListType(Class<?> elementClass) {
			String elType = elementClass.getSimpleName();
			String s = "";
				switch(listTypeFinder.listDepth) {
				case 1:
					s =  String.format("list<%s>", elType);
					break;
				case 2:
					s =  String.format("list<list<%s>>", elType);
					break;
				case 3:
					s =  String.format("list<list<list<%s>>>", elType);
					break;
				default:
					break;
				}
			return s;
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
		assertEquals("LIST List1:list<String>;roles:List1;", source);
	}

	@Test
	public void testListEnum() {
		List<String> fields = Arrays.asList("directions");
		String source = zc.createForClass(Person.class, fields);
		assertEquals("ENUM Direction:Direction;LIST List1:list<Direction>;directions:List1;", source);
	}

	@Test
	public void testAll() {
		List<String> fields = Arrays.asList("directions", "age", "name", "roles");
		String source = zc.createForClass(Person.class, fields);
		log(source);
//		assertEquals("ENUM Direction:Direction;LIST List1:list<Direction>;directions:List1;", source);
	}

	//--
	private ZCreator zc;
	
	@Before 
	public void init() {
		zc = new ZCreator(null);
	}

	private void log(String s) {
		System.out.println(s);
	}
	
}
