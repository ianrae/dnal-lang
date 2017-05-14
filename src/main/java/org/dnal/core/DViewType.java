package org.dnal.core;

import java.util.Map;

import org.dnal.compiler.parser.ast.ViewDirection;
import org.dnal.compiler.parser.ast.ViewFormatExp;

public class DViewType extends DStructType {
	private Map<String,String> namingMap; //key is left and value is right
	private Map<String,ViewFormatExp> fnMap;
	private String relatedTypeName;
	private ViewDirection direction;
	
	public DViewType(String name, DType baseType, OrderedMap orderedMap, Map<String, String> namingMap2, 
			Map<String, ViewFormatExp> fnMap, String relatedTypeName, ViewDirection direction) {
		super(Shape.STRUCT, name, baseType, orderedMap);
		this.namingMap = namingMap2;
		this.fnMap = fnMap;
		this.relatedTypeName = relatedTypeName;
		this.direction = direction;
	}
	
	public Map<String,String> getNamingMap() {
		return namingMap;
	}
	public Map<String,ViewFormatExp> getFnMap() {
		return fnMap;
	}


	public String getRelatedTypeName() {
		return relatedTypeName;
	}

	public ViewDirection getDirection() {
		return direction;
	}
	

}