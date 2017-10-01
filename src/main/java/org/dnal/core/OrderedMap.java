package org.dnal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrderedMap {
    public Map<String,DType> map = new TreeMap<>();
    public List<String> orderedList = new ArrayList<>();  //ordered by when added
    private Map<String,Boolean> optionalMap = new HashMap<>();
    private Map<String,Boolean> uniqueMap = new HashMap<>();
    
    public void add(String name, DType type, boolean optional, boolean unique) {
        map.put(name, type);
        optionalMap.put(name, optional);
        uniqueMap.put(name, unique);
        orderedList.add(name);
    }
    public boolean containsKey(String name) {
        return map.containsKey(name);
    }
    public boolean isOptional(String name) {
        Boolean bb = optionalMap.get(name);
        return (bb == null) ? false : bb;
    }
    public boolean isUnique(String name) {
        Boolean bb = uniqueMap.get(name);
        return (bb == null) ? false : bb;
    }
}
