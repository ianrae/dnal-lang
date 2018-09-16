package org.dnal.outputex;

import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;

public interface OutputGeneratorEx {

	void structType(DStructType dtype, String typeName, String parentTypeName);

	void enumType(DStructType enumType, String typeName);

	void listType(DListType listType, String typeName, String elementName);

	void mapType(DMapType mapType);

	void scalarType(DType dtype, String typeName, String parentName);

	void topLevelValue(String varName, DValue dval, String typeName);

}