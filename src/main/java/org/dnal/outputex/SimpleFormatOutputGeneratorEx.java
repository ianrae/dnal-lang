package org.dnal.outputex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.TypePair;
import org.dnal.core.nrule.NRule;

public class SimpleFormatOutputGeneratorEx implements TypeGeneratorEx, ValueGeneratorEx {
	private static final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public List<String> outputL = new ArrayList<>();

	private String getTypeName(DType dtype) {
		String typeName = dtype.getName();
		if (TypeInfo.isBuiltIntype(typeName)) {
			typeName = TypeInfo.parserTypeOf(typeName);
		} else {
			typeName = dtype.getCompleteName();
		}
		return typeName;
	}

	private String genIndent(int amount) {
		String space = "";
		for(int i = 0; i < amount; i++) {
			space += " ";
		}
		return space;
	}

	// -- types --
	@Override
	public void structType(DStructType structType, String typeName, String parentTypeName) {
		String s = String.format("type:%s:%s", typeName, parentTypeName);
		outputL.add(s);
		for(String fName: structType.orderedList()) {
			DType innerType = structType.getFields().get(fName);
			s = String.format(" %s:%s", fName, getTypeName(innerType));
			outputL.add(s);
		}
		outputL.add("endtype");
	}

	@Override
	public void enumType(DStructType enumType, String typeName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void listType(DListType listType, String typeName, String elementName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mapType(DMapType mapType, String typeName, String elementName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scalarType(DType dtype, String typeName, String parentName) {
		// TODO Auto-generated method stub

	}

	// -- values --
	@Override
	public void startStruct(String varName, String fieldName, DValue dval, DStructType structType, GeneratorContext genctx, int index) {
	}

	@Override
	public void endStruct(DValue dval, DStructType structType, GeneratorContext genctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startList(String varName, String fieldName, DValue dval, DListType listType, GeneratorContext genctx,
			int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endList(DValue dval, DListType listType, GeneratorContext genctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startMap(String varName, String fieldName, DValue dval, DMapType mapType, GeneratorContext genctx,
			int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endMap(DValue dval, DMapType mapType, GeneratorContext genctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void listElementValue(DValue dval, GeneratorContext genctx, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void structMemberValue(String fieldName, DValue dval, GeneratorContext genctx, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mapMemberValue(String key, DValue dval, GeneratorContext genctx, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scalarValue(String varName, DValue dval, GeneratorContext genctx) {
		// TODO Auto-generated method stub

	}


}