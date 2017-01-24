package org.dnal.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTypeHierarchy {

    private Map<DType, BitSet> parentsMap = new HashMap<>();
    private Map<DType, BitSet> childMap = new HashMap<>();
    
    public void build(Map<String,DType> allTypes) {
        
        for(String typeName: allTypes.keySet()) {
            DType dtype = allTypes.get(typeName);
            BitSet parentBS = new BitSet();
            BitSet childBS = new BitSet();
            buildBS(dtype, parentBS, childBS, allTypes);
            parentsMap.put(dtype, parentBS);
            childMap.put(dtype, childBS);
        }
    }

    private void buildBS(DType target, BitSet parentBS, BitSet childBS, Map<String, DType> allTypes) {
        for(String typeName: allTypes.keySet()) {
            DType dtype = allTypes.get(typeName);
            if (calcIsParent(target, dtype)) {
                //dtype is a base-class of target
                parentBS.set(dtype.getBitIndex());
            }
            
            if (calcIsChild(target, dtype)) {
                //dtype is sub-class of target
                childBS.set(dtype.getBitIndex());
            }
        }
    }
    private boolean calcIsParent(DType type, DType parent) {
        DType current = type.getBaseType();
        
        //!!add runaway check
        while(current != null) {
            if (current == parent) {
                return true;
            }
            current = current.getBaseType();
        }
        return false;
    }
    private boolean calcIsChild(DType target, DType child) {
        return calcIsParent(child, target);
    }
    
    public boolean isParent(DType type, DType parent) {
        BitSet bs = parentsMap.get(type);
        if (bs == null) {
            return false;
        }
        return bs.get(parent.getBitIndex());
    }
    public boolean isChild(DType type, DType child) {
        BitSet bs = childMap.get(type);
        if (bs == null) {
            return false;
        }
        return bs.get(child.getBitIndex());
    }

    public List<DType> findParentTypes(Map<String, DType> allTypes, DType type) {
        BitSet bs = parentsMap.get(type);
        if (bs == null) {
            return null;
        }
        return findInBitSet(bs, allTypes);
    }
    public List<DType> findChildTypes(Map<String, DType> allTypes, DType type) {
        BitSet bs = childMap.get(type);
        if (bs == null) {
            return null;
        }
        return findInBitSet(bs, allTypes);
    }

    private List<DType> findInBitSet(BitSet bs, Map<String, DType> allTypes) {
        List<DType> resultList = new ArrayList<>();
        for(String typeName: allTypes.keySet()) {
            DType dtype = allTypes.get(typeName);
            if (bs.get(dtype.getBitIndex())) {
                resultList.add(dtype);
            }
        }        
        return resultList;
    }
    
}
