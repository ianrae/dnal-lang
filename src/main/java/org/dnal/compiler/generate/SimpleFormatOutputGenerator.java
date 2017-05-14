package org.dnal.compiler.generate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRule;
import org.dnal.dnalc.ConfigFileOptions;

public class SimpleFormatOutputGenerator implements OuputGenerator {
    private static final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    public List<String> outputL = new ArrayList<>();
    private int inList; //0 means no
    private int inStruct; //0 means no
//    private Stack<String> shapeStack = new Stack<>();
    
    @Override
    public void startStructType(String name, DStructType dtype) {
        String baseTypeName = TypeInfo.getBaseTypeName(dtype, true);
        String completeName = dtype.getCompleteName();
        String s = String.format("type:%s:%s", completeName, baseTypeName);
        outputL.add(s);
    }
    @Override
    public void startEnumType(String name, DStructType dtype) {
        String baseTypeName = TypeInfo.getBaseTypeName(dtype, true);
        String completeName = dtype.getCompleteName();
        String s = String.format("type:%s:%s", completeName, baseTypeName);
        outputL.add(s);
    }
    @Override
    public void startType(String name, DType dtype) {
        String baseTypeName = TypeInfo.getBaseTypeName(dtype, true);
        String completeName = dtype.getCompleteName();
        String s = String.format("type:%s:%s", completeName, baseTypeName);
        outputL.add(s);
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

    @Override
    public void startListType(String name, DListType type) {
        String elType = getTypeName(type.getElementType());
        String baseTypeName = (type.getBaseType() == null) ? String.format("list<%s>", elType) : type.getBaseType().getName();
        String s = String.format("type:%s:%s", name, baseTypeName);
        outputL.add(s);
    }


    @Override
    public void endType(String name, DType type) {
        outputL.add("endtype");
    }

    @Override
    public void structMember(String name, DType type) {
        String s = String.format(" %s:%s", name, getTypeName(type));
        outputL.add(s);
    }

    @Override
    public void rule(int index, String ruleText, NRule rule) {
        String s = String.format(" r: %s", ruleText);
        outputL.add(s);
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
        } else if (parentVal.getType().isShape(Shape.LIST)) {
            return "L";
        } else {
            return "";
        }
    }
    @Override
    public void value(String name, DValue dval, DValue parentVal) {
        String s;
        String space = genIndent(inList + inStruct);
        
//        String shape = (shapeStack.isEmpty()) ? "" : shapeStack.peek();
        String shape = getShapeCode(parentVal);

        if (shape.equals("L")) {
            String strValue = DValToString(dval);
            s = String.format("%s%s", space, strValue);
        } else if (shape.equals("S")) {
            String strValue = DValToString(dval);
            s = String.format("%sv%s:%s", space, name, strValue);
        } else {
            String strValue = DValToString(dval);
            s = String.format("%svalue:%s:%s:%s", space, name, getTypeName(dval.getType()), strValue);
        }
        outputL.add(s);
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

    @Override
    public void enumMember(String name, DType memberType) {
        String s = String.format(" %s:%s", name, getTypeName(memberType));
        outputL.add(s);
    }

    @Override
    public void startList(String name, DValue value) {
        String s = String.format("value:%s:%s [", name, getTypeName(value.getType()));
        outputL.add(s);
        inList++;
//        shapeStack.push("L");
    }

    @Override
    public void endList(String name, DValue value) {
        outputL.add("]");
        inList--;
//        shapeStack.pop();
    }

    @Override
    public void startStruct(String name, DValue dval) {
        if (inList == 0) {
            String s = String.format("value:%s:%s {", name, dval.getType().getName());
            outputL.add(s);
        } else {
            String space = genIndent(inList);
            String s = String.format("%s{", space);
            outputL.add(s);
        }
        inStruct++;
//        shapeStack.push("S");
    }

    @Override
    public void endStruct(String name, DValue value) {
        if (inList == 0) {
            outputL.add("}");
        } else {
            String space = genIndent(inList);
            String s = String.format("%s}", space);
            outputL.add(s);
        }
        inStruct--;
//        shapeStack.pop();
    }

    @Override
    public void finish() {
    }
	@Override
	public void setOptions(ConfigFileOptions configFileOptions) {
	}
}