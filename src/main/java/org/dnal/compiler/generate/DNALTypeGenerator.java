package org.dnal.compiler.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.dnal.compiler.nrule.UniqueRule;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.TypePair;
import org.dnal.core.nrule.NRule;

public class DNALTypeGenerator implements TypeGenerator {
    public List<String> outputL = new ArrayList<>();
	
	@Override
	public void structType(DStructType dtype, String typeName, String parentTypeName) {
		String rulesStr = getRuleStr(dtype);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		String body = getStructMembers(dtype);
		String s = String.format("type %s %s {%s}%s end", typeName, parentTypeName, body, rulesStr);
		outputL.add(s);
	}
	protected String getStructMembers(DStructType dtype) {
		StringJoiner joiner = new StringJoiner(", ");
        for(TypePair pair: dtype.getAllFields()) {
        	String fieldTypeName = getFieldTypeName(dtype, pair); 
        	String optional = getFieldOptional(dtype, pair); 
        	String unique = getFieldUnique(dtype, pair); 
        	String s = getAllFields(dtype, pair, fieldTypeName, optional, unique);
            joiner.add(s); 
        }

        return joiner.toString();
	}

	protected String getAllFields(DStructType dtype, TypePair pair, String fieldTypeName, String optional,
			String unique) {
    	String s = String.format("%s %s%s%s", pair.name, fieldTypeName, optional, unique);
		return s;
	}
	protected String getFieldUnique(DStructType dtype, TypePair pair) {
    	String unique = dtype.fieldIsUnique(pair.name) ? " unique": "";
    	return unique;
	}
	protected String getFieldOptional(DStructType dtype, TypePair pair) {
    	String optional = dtype.fieldIsOptional(pair.name) ? " optional": "";
		return optional;
	}
	protected String getFieldTypeName(DStructType dtype, TypePair pair) {
    	String fieldTypeName = TypeInfo.parserTypeOf(pair.type.getName());
    	return fieldTypeName;
	}
	@Override
	public void enumType(DStructType enumType, String typeName) {
		String parentName = "enum";
		String rulesStr = getRuleStr(enumType);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		String body = getEnumMembers(enumType);
		String s = String.format("type %s %s {%s}%s end", typeName, parentName, body, rulesStr);
		outputL.add(s);
	}
	protected String getEnumMembers(DStructType enumType) {
		StringJoiner joiner = new StringJoiner(", ");
        for(String field: enumType.orderedList()) {
            joiner.add(field); 
        }

        return joiner.toString();
	}
	@Override
	public void listType(DListType listType, String typeName, String elementName) {
		String rulesStr = getRuleStr(listType);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		String s = String.format("type %s list<%s>%s end", typeName, elementName, rulesStr);
		outputL.add(s);
	}
	@Override
	public void mapType(DMapType mapType, String typeName, String elementName) {
		String rulesStr = getRuleStr(mapType);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		//type SizeMap map<int> end
		String s = String.format("type %s map<%s>%s end", typeName, elementName, rulesStr);
		outputL.add(s);
	}
	@Override
	public void scalarType(DType dtype, String typeName, String parentName) {
		String rulesStr = getRuleStr(dtype);
		if (! StringUtils.isEmpty(rulesStr)) {
			rulesStr = String.format(" %s", rulesStr);
		}
		String s = String.format("type %s %s%s end", typeName, parentName, rulesStr);
		outputL.add(s);
	}
	protected String getRuleStr(DType dtype) {
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
	public boolean finish() {
		return true;
	}
}