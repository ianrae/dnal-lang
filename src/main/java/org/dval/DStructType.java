package org.dval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DStructType extends DType {
//	private Map<String,DType> fields;
    private OrderedMap orderedMap;
	//!! add String naturalKeyField, for db query. eg. 'code'
    private List<TypePair> allFields; //lazy-created

	
	public DStructType(Shape shape, String name, DType baseType, OrderedMap orderedMap) {
		super(shape, name, baseType);
		this.orderedMap = orderedMap;
	}
	
	public boolean fieldIsOptional(String fieldname) {
	    return orderedMap.isOptional(fieldname);
	}
    public boolean fieldIsUnique(String fieldname) {
        return orderedMap.isUnique(fieldname);
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

    private List<TypePair> doAllFieldsForType(DStructType dtype) {
        DStructType baseType = (DStructType) dtype.getBaseType();
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