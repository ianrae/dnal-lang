package org.dnal.api.bean2;

import java.lang.reflect.Method;

public class FieldInfo {
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