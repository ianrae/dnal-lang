package org.dnal.outputex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dnal.compiler.nrule.UniqueRule;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRule;

public class SimpleFormatOutputGeneratorEx implements TypeGeneratorEx, ValueGeneratorEx {
	private static final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public List<String> outputL = new ArrayList<>();

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
		addRules(structType);
		outputL.add("endtype");
	}

	@Override
	public void enumType(DStructType enumType, String typeName) {
		String s = String.format("type:%s:enum", typeName);
		outputL.add(s);
		for(String fName: enumType.orderedList()) {
			DType innerType = enumType.getFields().get(fName);
			s = String.format(" %s:%s", fName, getTypeName(innerType));
			outputL.add(s);
		}
		addRules(enumType);
		outputL.add("endtype");
	}

	@Override
	public void listType(DListType listType, String typeName, String elementName) {
		String s = String.format("type:%s:list<%s>", typeName, elementName);
		outputL.add(s);
		addRules(listType);
		outputL.add("endtype");
	}

	@Override
	public void mapType(DMapType mapType, String typeName, String elementName) {
		String s = String.format("type:%s:map<%s>", typeName, elementName);
		outputL.add(s);
		addRules(mapType);
		outputL.add("endtype");
	}

	@Override
	public void scalarType(DType dtype, String typeName, String parentName) {
		String s = String.format("type:%s:%s", typeName, parentName);
		outputL.add(s);
		addRules(dtype);
		outputL.add("endtype");
	}

	// -- values --
	@Override
	public void startStruct(ValuePlacement placement, DValue dval, DStructType structType, GeneratorContext genctx, int index) {
		if (placement.isTopLevelValue) {
			String typeName = getTypeName(dval.getType());
			String s = String.format("value:%s:%s {", placement.name, typeName);
			outputL.add(s);
		} else if (placement.name == null) {
			String s = String.format(" {");
			outputL.add(s);
		} else {
			String s = String.format(" v%s {", placement.name);
			outputL.add(s);
		}
	}

	@Override
	public void endStruct(ValuePlacement placement, DValue dval, DStructType structType, GeneratorContext genctx) {
		outputL.add("}");
	}

	@Override
	public void startList(ValuePlacement placement, DValue dval, DListType listType, GeneratorContext genctx, int index) {
		if (placement.isTopLevelValue) {
			String typeName = getTypeName(dval.getType());
			String s = String.format("value:%s:%s [", placement.name, typeName);
			outputL.add(s);
		} else if (placement.name == null) {
			String s = String.format(" [");
			outputL.add(s);
		} else {
			String s = String.format(" v%s [", placement.name);
			outputL.add(s);
		}
	}

	@Override
	public void endList(ValuePlacement placement, DValue dval, DListType listType, GeneratorContext genctx) {
		outputL.add("]");
	}

	@Override
	public void startMap(ValuePlacement placement, DValue dval, DMapType mapType, GeneratorContext genctx, int index) {
		if (placement.isTopLevelValue) {
			String typeName = getTypeName(dval.getType());
			String s = String.format("value:%s:%s {", placement.name, typeName);
			outputL.add(s);
		} else if (placement.name == null) {
			String s = String.format(" {");
			outputL.add(s);
		} else {
			String s = String.format(" v%s {", placement.name);
			outputL.add(s);
		}
	}

	@Override
	public void endMap(ValuePlacement placement, DValue dval, DMapType mapType, GeneratorContext genctx) {
		outputL.add("}");
	}

	@Override
	public void listElementValue(DValue dval, GeneratorContext genctx, int index) {
		String value = DValToString(dval);
		String s = String.format(" %s", value);
		outputL.add(s);
	}

	@Override
	public void structMemberValue(String fieldName, DValue dval, GeneratorContext genctx, int index) {
		String value = DValToString(dval);
		String s = String.format(" v%s:%s", fieldName, value);
		outputL.add(s);
	}

	@Override
	public void mapMemberValue(String key, DValue dval, GeneratorContext genctx, int index) {
		String value = DValToString(dval);
		String s = String.format(" v%s:%s", key, value);
		outputL.add(s);
	}

	@Override
	public void scalarValue(String varName, DValue dval, GeneratorContext genctx) {
		if (varName != null) {
			String typeName = getTypeName(dval.getType());
			String value = DValToString(dval);
			String s = String.format("value:%s:%s:%s", varName, typeName, value);
			outputL.add(s);
		}
	}

	//--helpers--
	private void addRules(DType dtype) {
        for(NRule rule: dtype.getRawRules()) {
            String ruleText = rule.getRuleText();
            if (rule instanceof UniqueRule) {
            	ruleText= String.format("unique %s", ruleText); 
            }
            outputL.add(" r: " + ruleText); 
        }
	}
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
    private String DValToString(DValue dval) {
        if (dval == null) {
            return "null";
        }
        Object obj = dval.getObject();
        if (obj instanceof Date) {
            String s = df1.format(obj);
            return s;
        } else {
            return obj.toString();
        }
    }
	private void appendCurrentList(String str) {
		String s = outputL.remove(outputL.size() - 1);
		outputL.add(s + str);
	}


}