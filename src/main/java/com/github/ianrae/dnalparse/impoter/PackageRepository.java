package com.github.ianrae.dnalparse.impoter;

import java.util.HashMap;
import java.util.Map;

public class PackageRepository {
    private Map<String, Object> map = new HashMap<>();
    
    public void addPackage(String pkg, Object obj) {
        map.put(pkg, obj);
    }
    public boolean exists(String pkg) {
        return map.containsKey(pkg);
    }
}