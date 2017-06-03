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
import org.dnal.api.bean.ClassX;
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
		public boolean haveResolvedStruct;
		
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
		private List<FieldInfo> outputFieldList = new ArrayList<>();
		private List<FieldInfo> genList = new ArrayList<>();
		private BeanMethodInvoker finder = new BeanMethodInvoker();
		private int nextListNameId = 1;

		public ZCreator(XErrorTracker et) {
			this.et = et;
			listTypeFinder = new ListTypeFinder(et);
			builder = new BeanToDTypeBuilder(et);
		}
		public boolean createForClass(Class<?> clazz, List<String> fields) {
			for(String fieldName: fields) {
				FieldInfo finfo = new FieldInfo(clazz, fieldName);
				stack.push(finfo);
				outputFieldList.add(finfo);
			}
			
			//now resolve all types on the stack
			int retries = 0;
			while(! stack.isEmpty())  {
				if (retries > 10) {
					et.addParsingError("retry runaway!");
					break; //!!error
				}
				
				retries++;
				FieldInfo finfo = stack.peek();
				if (resolve(finfo)) {
					stack.pop();
					retries = 0;
				}
			}
			
			return et.areNoErrors();
		}
		
		public String getOutput() {
			String src = "";
			src += doOutput(genList);
			src += doOutput(outputFieldList);
			return src;	
		}
		
		private String doOutput(List<FieldInfo> list) {
			String src = "";
			for(FieldInfo fino: list) {
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
			
			et.addParsingError(String.format("bad field '%s'", finfo.fieldName));
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
				
				//we will duplicate similar types, eg list<string>
				FieldInfo newInfo = new FieldInfo(clazz, finfo.dnalTypeName);
				newInfo.isList = true;
				newInfo.dnalTypeName = calculateListType(elementClazz);
				genList.add(newInfo);
				return true;
			}
			
			if (clazz.isEnum()) {
				String existingType = findAlreadyDefinedType(clazz);
				if (existingType == null) {
					FieldInfo newInfo = new FieldInfo(clazz, finfo.fieldName);
					newInfo.isEnum = true;
					newInfo.dnalTypeName = clazz.getSimpleName();
					genList.add(newInfo);
					finfo.dnalTypeName = newInfo.dnalTypeName;
				} else {
					finfo.dnalTypeName = existingType;
				}
				return true;
			}
			
			String className = builder.getPrimitive(clazz);
			if (className != null) {
				finfo.dnalTypeName = className;
				return true;
			}
			
			//else struct
			if (! finfo.haveResolvedStruct) {
				finfo.haveResolvedStruct = true;
				BeanMethodCache structMethodCache = finder.getAllGetters(clazz);
				for(String inner: structMethodCache.keySet()) {
					FieldInfo newInfo = new FieldInfo(clazz, inner);
					stack.push(newInfo);
				}
				finfo.dnalTypeName = clazz.getSimpleName();
				return false;
			} else {
				FieldInfo newInfo = new FieldInfo(clazz, clazz.getSimpleName());
				newInfo.dnalTypeName = newInfo.fieldName;
				genList.add(newInfo);
				return true;
			}
			
		}
		
		private boolean alreadyDefined(Class<?> clazz) {
			return (findAlreadyDefinedType(clazz) != null);
		}
		private String findAlreadyDefinedType(Class<?> clazz) {
			String className = builder.getPrimitive(clazz);
			if (className != null) {
				return className;
			}
			
			String target = clazz.getSimpleName();
			for(FieldInfo finfo: genList) {
				if (target.equals(finfo.dnalTypeName)) {
					return finfo.dnalTypeName;
				}
			}
			return null;
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
		String source = createForClass(Person.class, fields);
		assertEquals("name:string;age:int;", source);
		chkSuccess();
	}

	@Test
	public void testList() {
		List<String> fields = Arrays.asList("roles");
		String source = createForClass(Person.class, fields);
		assertEquals("LIST List1:list<String>;roles:List1;", source);
		chkSuccess();
	}

	@Test
	public void testListEnum() {
		List<String> fields = Arrays.asList("directions");
		String source = createForClass(Person.class, fields);
		assertEquals("ENUM Direction:Direction;LIST List1:list<Direction>;directions:List1;", source);
		chkSuccess();
	}

	@Test
	public void testAll() {
		List<String> fields = Arrays.asList("directions", "age", "name", "roles");
		String source = createForClass(Person.class, fields);
		log(source);
		chkSuccess();
	}
	
	@Test
	public void testX1() {
		List<String> fields = Arrays.asList("direction1", "dirlist1");
		String source = createForClass(ClassX.class, fields);
		chkSuccess();
		assertEquals("ENUM Direction:Direction;LIST List1:list<Direction>;direction1:Direction;dirlist1:List1;", source);
	}
	@Test
	public void testX1Inner() {
		List<String> fields = Arrays.asList("person1");
		String source = createForClass(ClassX.class, fields);
		chkSuccess();
		String s = "LIST List1:list<String>;ENUM Direction:Direction;LIST List2:list<Direction>;Person:Person;person1:Person;";
		assertEquals(s, source);
	}
	@Test
	public void testPersonList() {
		List<String> fields = Arrays.asList("personList");
		String source = createForClass(ClassX.class, fields);
		chkSuccess();
		log(source);
		String s = "LIST List1:list<String>;ENUM Direction:Direction;LIST List2:list<Direction>;Person:Person;LIST List3:list<Person>;personList:List3;";
		assertEquals(s, source);
	}
	

	//--
	private ZCreator zc;
	private XErrorTracker et = new XErrorTracker();
	
	@Before 
	public void init() {
		zc = new ZCreator(et);
	}
	
	private void chkSuccess() {
		if (et.areErrors()) {
			et.dumpErrors();
		}
		assertEquals(false, et.areErrors());
	}

	private String createForClass(Class<?> clazz, List<String> fields) {
		boolean b = zc.createForClass(clazz, fields);
		chkSuccess();
		assertEquals(true, b);
		return zc.getOutput();
	}

	

	private void log(String s) {
		System.out.println(s);
	}
	
}
