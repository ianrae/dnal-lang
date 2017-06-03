package org.dnal.api.beancopier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.dnal.api.bean.BeanMethodCache;
import org.dnal.compiler.et.XErrorTracker;

public class DnalTypeDiscoverer {
	private ListTypeFinder listTypeFinder;
	private XErrorTracker et;
	private BeanToDTypeBuilder builder;
	private Stack<FieldInfo> stack = new Stack<>();
	private List<FieldInfo> outputFieldList = new ArrayList<>();
	private List<FieldInfo> genList = new ArrayList<>();
	private BeanMethodInvoker finder = new BeanMethodInvoker();
	private int nextListNameId = 1;

	public DnalTypeDiscoverer(XErrorTracker et) {
		this.et = et;
		listTypeFinder = new ListTypeFinder(et);
		builder = new BeanToDTypeBuilder(et);
	}
	public boolean createForClass(Class<?> clazz, List<String> fields, boolean isSourceClass) {
		for(String fieldName: fields) {
			FieldInfo finfo = new FieldInfo(clazz, fieldName);
			finfo.isSourceClass = isSourceClass;
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
	
	public String getGenOutput() {
		String src = "";
		src += doOutput(genList);
		return src;	
	}
	public String getOutputFieldsOutput() {
		String src = "";
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
	
	public Class<?> getElementClassIfList(Method meth) {
		Class<?> clazz = meth.getReturnType();
		if (Collection.class.isAssignableFrom(clazz)) {
			Class<?> elementClazz = listTypeFinder.getListElementType(meth, clazz);
			return elementClazz;
		}
		return null;
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
		if (alreadyDefined(clazz)) {
			finfo.dnalTypeName = clazz.getSimpleName();
			return true;
		}
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
	public String findAlreadyDefinedType(Class<?> clazz) {
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
		String elType = findAlreadyDefinedType(elementClass);
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
	public List<FieldInfo> getOutputFieldList() {
		return outputFieldList;
	}
	public List<FieldInfo> getGenList() {
		return genList;
	}
	
}