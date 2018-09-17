package org.dnal.outputex;

import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DValue;

public interface ValueGeneratorEx {

	void startStruct(ValuePlacement placement, DValue dval, DStructType structType, GeneratorContext genctx, int index);
	void endStruct(ValuePlacement placement, DValue dval, DStructType structType, GeneratorContext genctx);

	void startList(ValuePlacement placement, DValue dval, DListType listType, GeneratorContext genctx, int index);
	void endList(ValuePlacement placement, DValue dval, DListType listType, GeneratorContext genctx);
	
	void startMap(ValuePlacement placement, DValue dval, DMapType mapType, GeneratorContext genctx, int index);
	void endMap(ValuePlacement placement, DValue dval, DMapType mapType, GeneratorContext genctx);
	
	void listElementValue(DValue dval, GeneratorContext genctx, int index);
	
	/**
	 * 
	 * @param fieldName never-null
	 * @param dval
	 * @param genctx
	 * @param index
	 */
	void structMemberValue(String fieldName, DValue dval, GeneratorContext genctx, int index);
	void mapMemberValue(String key, DValue dval, GeneratorContext genctx, int index);
	
	/**
	 * 
	 * @param varName   never-null
	 * @param dval
	 * @param genctx
	 */
	void scalarValue(String varName, DValue dval, GeneratorContext genctx);
}