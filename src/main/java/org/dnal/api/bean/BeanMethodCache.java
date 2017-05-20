package org.dnal.api.bean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

//---
public class BeanMethodCache {
    private Map<String,Method> map = new HashMap<>();
    
    public void add(String fieldName, Method meth) {
        map.put(fieldName, meth);
    }
    public Method getMethod(String fieldName) {
        return map.get(fieldName);
    }
    public int size() {
    	return map.size();
    }
}