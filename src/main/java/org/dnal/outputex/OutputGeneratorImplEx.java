package org.dnal.outputex;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang.StringUtils;
import org.dnal.compiler.nrule.UniqueRule;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.TypePair;
import org.dnal.core.nrule.NRule;

public class OutputGeneratorImplEx implements OutputGeneratorEx {
    public List<String> outputL = new ArrayList<>();
	public boolean generateTypes = false;
	public boolean generateValues = false;
	
	@Override
	public void structType(DStructType dtype, String typeName, String parentTypeName) {
		if (!generateTypes) {
			return;
		}
		String rulesStr = getRuleStr(dtype);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		String body = getStructMembers(dtype);
		String s = String.format("type %s %s {%s}%s end", typeName, parentTypeName, body, rulesStr);
		outputL.add(s);
	}
	private String getStructMembers(DStructType dtype) {
		StringJoiner joiner = new StringJoiner(", ");
        for(TypePair pair: dtype.getAllFields()) {
        	String field = pair.name;
        	String fieldTypeName = TypeInfo.parserTypeOf(pair.type.getName());
        	String optional = (dtype.fieldIsOptional(field)) ? " optional": "";
        	String unique = (dtype.fieldIsUnique(field)) ? " unique": "";
        	String s = String.format("%s %s%s%s", field, fieldTypeName, optional, unique);
            joiner.add(s); 
        }

        return joiner.toString();
	}



	@Override
	public void enumType(DStructType enumType, String typeName) {
		if (!generateTypes) {
			return;
		}
		String parentName = "enum";
		String rulesStr = getRuleStr(enumType);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		String body = getEnumMembers(enumType);
		String s = String.format("type %s %s {%s}%s end", typeName, parentName, body, rulesStr);
		outputL.add(s);
	}
	private String getEnumMembers(DStructType enumType) {
		StringJoiner joiner = new StringJoiner(", ");
        for(String field: enumType.orderedList()) {
            joiner.add(field); 
        }

        return joiner.toString();
	}
	@Override
	public void listType(DListType listType, String typeName, String elementName) {
		if (!generateTypes) {
			return;
		}
		String rulesStr = getRuleStr(listType);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		String s = String.format("type %s list<%s>%s end", typeName, elementName, rulesStr);
		outputL.add(s);
	}
	@Override
	public void mapType(DMapType mapType) {
		if (!generateTypes) {
			return;
		}
		// TODO Auto-generated method stub
		
	}
	@Override
	public void scalarType(DType dtype, String typeName, String parentName) {
		if (!generateTypes) {
			return;
		}
		String rulesStr = getRuleStr(dtype);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		String s = String.format("type %s %s%s end", typeName, parentName, rulesStr);
		outputL.add(s);
	}
	private String getRuleStr(DType dtype) {
		StringJoiner joiner = new StringJoiner(" ");
        for(NRule rule: dtype.getRawRules()) {
            String ruleText = rule.getRuleText();
            if (rule instanceof UniqueRule) {
            	ruleText= String.format("unique %s", ruleText); 
            }
            joiner.add(ruleText); 
        }

        return joiner.toString();
	}
	@Override
	public void topLevelValue(String varName, DValue dval, String typeName) {
		if (!generateValues) {
			return;
		}
		
		String valueStr = getValueStr(dval);
		String s = String.format("let %s %s = %s", varName, typeName, valueStr);
		
		outputL.add(s);
	}
	private String getValueStr(DValue dval) {
		if (dval.getObject() == null) {
			return "null";
		}
		
		String s = null;
		DType dtype = dval.getType();
		if (dtype.isScalarShape()) {
			switch (dval.getType().getShape()) {
				case BOOLEAN:
					s = Boolean.valueOf(dval.asBoolean()).toString();
					break;
				case DATE:
					s = Long.valueOf(dval.asDate().getTime()).toString(); //??use sdf formatter??
					break;
				case INTEGER:
					s = Integer.valueOf(dval.asInt()).toString();
					break;
				case LONG:
					s = Long.valueOf(dval.asLong()).toString();
					break;
				case NUMBER:
					s = Double.valueOf(dval.asNumber()).toString();
					break;
				case STRING:
					//add code to use either ' or "!!
					s = String.format("'%s'", dval.asString());
					break;
				case ENUM:
					s = doEnum(dval, dtype);
				default:
					break;
			}
		} else if (dtype.isListShape()) {
			DListType listType = (DListType) dtype;
			s = doList(dval, listType);
		} else if (dtype.isStructShape()) {
			DStructType structType = (DStructType) dtype;
			s = doStruct(dval, structType);
		} else if (dtype.isMapShape()) {
			DMapType mapType = (DMapType) dtype;
			s = doMap(dval, mapType);
		}
		return s;
	}
	private String doMap(DValue dval, DMapType mapType) {
		StringJoiner joiner = new StringJoiner(", ");
		//!!should fields be in alpha order?
		for(String fieldName: dval.asMap().keySet()) {
			DValue inner = dval.asMap().get(fieldName);
			String s = getValueStr(inner);
			String fieldStr = String.format("%s:%s", fieldName, s);
			joiner.add(fieldStr);
		}
		return String.format("{%s}", joiner.toString());
	}
	private String doStruct(DValue dval, DStructType structType) {
		StringJoiner joiner = new StringJoiner(", ");
		//!!should fields be in alpha order?
		for(String fieldName: dval.asStruct().getFieldNames()) {
			DValue inner = dval.asStruct().getField(fieldName);
			String s = getValueStr(inner);
			String fieldStr = String.format("%s:%s", fieldName, s);
			joiner.add(fieldStr);
			
		}
		return String.format("{%s}", joiner.toString());
	}
	private String doEnum(DValue dval, DType dtype) {
		DStructType enumType = (DStructType) dtype;
		return dval.asString();
	}
	private String doList(DValue dval, DListType listType) {
		StringJoiner joiner = new StringJoiner(", ");
		for(DValue inner: dval.asList()) {
			String s = getValueStr(inner);
			joiner.add(s);
			
		}
		return String.format("[%s]", joiner.toString());
	}
}