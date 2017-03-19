package org.dnal.core;

import java.util.Map;

public class DViewType extends DStructType {
	private Map<String,String> namingMap; //key is left and value is right
	private String relatedTypeName;
	
	public DViewType(String name, DType baseType, OrderedMap orderedMap, Map<String, String> namingMap2, String relatedTypeName) {
		super(Shape.STRUCT, name, baseType, orderedMap);
		this.namingMap = namingMap2;
		this.relatedTypeName = relatedTypeName;
	}
	
	public Map<String,String> getNamingMap() {
		return namingMap;
	}

	public String getRelatedTypeName() {
		return relatedTypeName;
	}
	

}