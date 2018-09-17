package org.dnal.outputex;

import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DValue;

public interface ValueGeneratorEx {

	void startStruct(String varName, String fieldName, DValue dval, DStructType structType, GeneratorContext genctx, int index);
	void endStruct(DValue dval, DStructType structType, GeneratorContext genctx);

	void startList(String varName, String fieldName, DValue dval, DListType listType, GeneratorContext genctx, int index);
	void endList(DValue dval, DListType listType, GeneratorContext genctx);
	
	void startMap(String varName, String fieldName, DValue dval, DMapType mapType, GeneratorContext genctx, int index);
	void endMap(DValue dval, DMapType mapType, GeneratorContext genctx);
	
	void listElementValue(DValue dval, GeneratorContext genctx, int index);
	void structMemberValue(String fieldName, DValue dval, GeneratorContext genctx, int index);
	void mapMemberValue(String key, DValue dval, GeneratorContext genctx, int index);
	
	void scalarValue(String varName, String fieldName, DValue dval, GeneratorContext genctx);
}