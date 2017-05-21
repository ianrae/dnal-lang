package org.dnal.api.beancopier;

import java.util.List;

public class BeanCopierContextKey {
	public Object sourceObj;
	public Object destObj;
	public List<FieldSpec> fieldL;
	
	public BeanCopierContextKey(Object dto, Object x, List<FieldSpec> fieldL) {
		super();
		this.sourceObj = dto;
		this.destObj = x;
		this.fieldL = fieldL;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof BeanCopierContextKey)) {
			return false;
		}
		
		BeanCopierContextKey other = (BeanCopierContextKey) obj;
		if (! isSameClass(sourceObj, other.sourceObj)) {
			return false;
		}
		if (! isSameClass(destObj, other.destObj)) {
			return false;
		}
		
		if (! fieldL.equals(other.fieldL)) {
			return false;
		}
		return true;
	}

	private boolean isSameClass(Object obj1, Object obj2) {
		String s1 = obj1.getClass().getName();
		String s2 = obj2.getClass().getName();
		return s1.equals(s2);
	}
}