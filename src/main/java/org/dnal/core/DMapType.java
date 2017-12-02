package org.dnal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DMapType extends DType {
    private OrderedMap orderedMap;
    private DType elementType;

	
	public DMapType(Shape shape, String name, DType baseType, DType elementType) {
		super(shape, name, baseType);
		this.elementType = elementType;
	}
	
//	public Map<String, DType> getFields() {
//		return orderedMap.map;
//	}
//	public List<String> orderedList() {
//	    return orderedMap.orderedList;
//	}
	
//	//not thread-safe!!
//    public List<TypePair> getAllFields() {
//        if (allFields == null) {
//            allFields = doAllFieldsForType(this);
//        }
//        return allFields;
//    }
//
//    private List<TypePair> doAllFieldsForType(DMapType dtype) {
//        DMapType baseType = (DMapType) dtype.getBaseType();
//        if (baseType == null) {
//            List<TypePair> list = new ArrayList<>();
//            for(String fieldName: dtype.orderedList()) {
//                DType field = dtype.getFields().get(fieldName);
//                list.add(new TypePair(fieldName, field));
//            }
//            return list;
//        } else {
//            List<TypePair> list = doAllFieldsForType(baseType);  //**recursion**
//            for(String fieldName: dtype.orderedList()) {
//                DType field = dtype.getFields().get(fieldName);
//                list.add(new TypePair(fieldName, field));
//            }
//            return list;
//        }
//    }

	public DType getElementType() {
		return elementType;
	}
	

}