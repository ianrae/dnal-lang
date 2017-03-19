package org.dnal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DViewType extends DType {
    private OrderedMap orderedMap;
    private List<TypePair> allFields; //lazy-created

	
	public DViewType(String name, DType baseType, OrderedMap orderedMap) {
		super(Shape.STRUCT, name, baseType);
		this.orderedMap = orderedMap;
	}
	
	public boolean fieldIsOptional(String fieldname) {
	    return orderedMap.isOptional(fieldname);
	}

	public Map<String, DType> getFields() {
		return orderedMap.map;
	}
	public List<String> orderedList() {
	    return orderedMap.orderedList;
	}
	
	//not thread-safe!!
    public List<TypePair> getAllFields() {
        if (allFields == null) {
            allFields = doAllFieldsForType(this);
        }
        return allFields;
    }

    private List<TypePair> doAllFieldsForType(DViewType dtype) {
        DViewType baseType = (DViewType) dtype.getBaseType();
        if (baseType == null) {
            List<TypePair> list = new ArrayList<>();
            for(String fieldName: dtype.orderedList()) {
                DType field = dtype.getFields().get(fieldName);
                list.add(new TypePair(fieldName, field));
            }
            return list;
        } else {
            List<TypePair> list = doAllFieldsForType(baseType);  //**recursion**
            for(String fieldName: dtype.orderedList()) {
                DType field = dtype.getFields().get(fieldName);
                list.add(new TypePair(fieldName, field));
            }
            return list;
        }
    }
	

}