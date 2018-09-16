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

public class SimpleFormatOutputGeneratorEx implements OutputGeneratorEx {
    private static final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    public List<String> outputL = new ArrayList<>();
    private int inList; //0 means no
    private int inStruct; //0 means no
    private int inMap; //0 means no
    
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
    
    private String getShapeCode(DValue parentVal) {
        if (parentVal == null) {
            return "";
        } else if (parentVal.getType().isStructShape()) {
            return "S";
        } else if (parentVal.getType().isListShape()) {
            return "L";
        } else if (parentVal.getType().isMapShape()) {
            return "M";
        } else {
            return "";
        }
    }
//    @Override
//    public void value(String name, DValue dval, DValue parentVal) {
//        String s;
//        String space = genIndent(inList + inStruct + inMap);
//        
////        String shape = (shapeStack.isEmpty()) ? "" : shapeStack.peek();
//        String shape = getShapeCode(parentVal);
//
//        if (shape.equals("L")) {
//            String strValue = DValToString(dval);
//            s = String.format("%s%s", space, strValue);
//        } else if (shape.equals("S") || shape.equals("M")) {
//            String strValue = DValToString(dval);
//            s = String.format("%sv%s:%s", space, name, strValue);
//        } else {
//            String strValue = DValToString(dval);
//            s = String.format("%svalue:%s:%s:%s", space, name, getTypeName(dval.getType()), strValue);
//        }
//        outputL.add(s);
//    }
    
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

//    @Override
//    public void startList(String name, DValue value) {
//        String s = String.format("value:%s:%s [", name, getTypeName(value.getType()));
//        outputL.add(s);
//        inList++;
////        shapeStack.push("L");
//    }
//
//    @Override
//    public void endList(String name, DValue value) {
//        outputL.add("]");
//        inList--;
////        shapeStack.pop();
//    }
//
//    @Override
//    public void startStruct(String name, DValue dval) {
//        if (inList == 0) {
//            String s = String.format("value:%s:%s {", name, dval.getType().getName());
//            outputL.add(s);
//        } else {
//            String space = genIndent(inList);
//            String s = String.format("%s{", space);
//            outputL.add(s);
//        }
//        inStruct++;
////        shapeStack.push("S");
//    }
//
//    @Override
//    public void endStruct(String name, DValue value) {
//        if (inList == 0) {
//            outputL.add("}");
//        } else {
//            String space = genIndent(inList);
//            String s = String.format("%s}", space);
//            outputL.add(s);
//        }
//        inStruct--;
////        shapeStack.pop();
//    }
//
//    @Override
//    public void finish() {
//    }
//	@Override
//	public void setOptions(ConfigFileOptions configFileOptions) {
//	}
//
//	@Override
//	public void startMapType(String name, DMapType type) throws Exception {
//        String elType = getTypeName(type.getElementType());
//        String baseTypeName = (type.getBaseType() == null) ? String.format("map<%s>", elType) : type.getBaseType().getName();
//        String s = String.format("type:%s:%s", name, baseTypeName);
//        outputL.add(s);
//	}
//	@Override
//	public void startMap(String name, DValue value) throws Exception {
//        String s = String.format("value:%s:%s {", name, getTypeName(value.getType()));
//        outputL.add(s);
//        inMap++;
//	}
//	@Override
//	public void endMap(String name, DValue value) throws Exception {
//        outputL.add("}");
//        inMap--;
//	}
	
	/////////////////////////////////////////////////////
	@Override
	public void structType(DStructType dtype, String typeName, String parentTypeName) {
        String baseTypeName = TypeInfo.getBaseTypeName(dtype, true);
        String completeName = dtype.getCompleteName();
        String s = String.format("type:%s:%s", completeName, baseTypeName);
        for(TypePair pair: dtype.getAllFields()) {
            String ss = String.format(" %s:%s", pair.name, getTypeName(pair.type));
            outputL.add(ss);
        }
        outputL.add(s);
        endType(dtype);
	}
	
	private void endType(DType dtype) {
        int index = 0;
        for(NRule rule: dtype.getRawRules()) {
            String ruleText = rule.getRuleText();
            //visitor.rule(index++, ruleText, rule); //fix later!! need ruleText
            String s = String.format(" r: %s", ruleText);
            outputL.add(s);
            index++;
        }
		
        outputL.add("endtype");
	}
	@Override
	public void enumType(DStructType enumType, String typeName) {
        String baseTypeName = TypeInfo.getBaseTypeName(enumType, true);
        String completeName = enumType.getCompleteName();
        String s = String.format("type:%s:%s", completeName, baseTypeName);
        outputL.add(s);
        for(String key: enumType.orderedList()) {
            DType elType = enumType.getFields().get(key);
            String s2 = String.format(" %s:%s", key, getTypeName(elType));
            outputL.add(s2);
        }        
        endType(enumType);
	}
	@Override
	public void listType(DListType listType, String typeName, String elementName) {
        String elType = getTypeName(listType.getElementType());
        String baseTypeName = (listType.getBaseType() == null) ? String.format("list<%s>", elType) : listType.getBaseType().getName();
        String s = String.format("type:%s:%s", typeName, baseTypeName);
        outputL.add(s);
        endType(listType);
	}
	@Override
	public void mapType(DMapType mapType) {
        String baseTypeName = TypeInfo.getBaseTypeName(mapType, true);
        String completeName = mapType.getCompleteName();
        String s = String.format("type:%s:%s", completeName, baseTypeName);
        outputL.add(s);
        endType(mapType);
	}
	@Override
	public void scalarType(DType dtype, String typeName, String parentName) {
        String baseTypeName = TypeInfo.getBaseTypeName(dtype, true);
        String completeName = dtype.getCompleteName();
        String s = String.format("type:%s:%s", completeName, baseTypeName);
        outputL.add(s);
        endType(dtype);
	}
//	@Override
//	public void topLevelValue(String varName, DValue dval, String typeName) {
//        String s;
//        String space = genIndent(inList + inStruct + inMap);
//        
////        String shape = (shapeStack.isEmpty()) ? "" : shapeStack.peek();
//        String shape = getShapeCode(parentVal);
//
//        if (shape.equals("L")) {
//            String strValue = DValToString(dval);
//            s = String.format("%s%s", space, strValue);
//        } else if (shape.equals("S") || shape.equals("M")) {
//            String strValue = DValToString(dval);
//            s = String.format("%sv%s:%s", space, name, strValue);
//        } else {
//            String strValue = DValToString(dval);
//            s = String.format("%svalue:%s:%s:%s", space, name, getTypeName(dval.getType()), strValue);
//        }
//        outputL.add(s);
//	}

	@Override
	public void startStructValue(String varName, String fieldName, DValue dval, DStructType structType, GeneratorContext genctx, int index) {
      String s = String.format("value:%s:%s {", varName, getTypeName(dval.getType()));
      outputL.add(s);
	}

	@Override
	public void endStructValue(DValue dval, DStructType structType, GeneratorContext genctx) {
		outputL.add("}");
	}

	@Override
	public void startListValue(String varName, String fieldName, DValue dval, DListType listType, GeneratorContext genctx, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endListValue(DValue dval, DListType listType, GeneratorContext genctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startMapValue(String varName, String fieldName, DValue dval, DMapType mapType, GeneratorContext genctx, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endMapValue(DValue dval, DMapType mapType, GeneratorContext genctx) {
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
	public void scalarValue(String varName, DValue dval, GeneratorContext genctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mapMemberValue(String key, DValue dval, GeneratorContext genctx, int index) {
		// TODO Auto-generated method stub
		
	}
}