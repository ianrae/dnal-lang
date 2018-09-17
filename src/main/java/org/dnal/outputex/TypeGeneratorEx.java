package org.dnal.outputex;

import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;

public interface TypeGeneratorEx {

	void structType(DStructType dtype, String typeName, String parentTypeName);

	void enumType(DStructType enumType, String typeName);

	void listType(DListType listType, String typeName, String elementName);

	void mapType(DMapType mapType, String typeName, String elementName);

	void scalarType(DType dtype, String typeName, String parentName);
	
	boolean finish();
}