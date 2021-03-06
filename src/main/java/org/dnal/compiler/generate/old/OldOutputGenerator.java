package org.dnal.compiler.generate.old;

import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRule;
import org.dnal.dnalc.ConfigFileOptions;

public interface OldOutputGenerator {
	void setOptions(ConfigFileOptions configFileOptions);
	
    void startStructType(String name, DStructType dtype) throws Exception;
    void startEnumType(String name, DStructType dtype) throws Exception;
    void startType(String name, DType dtype) throws Exception;
    void startListType(String name, DListType type) throws Exception;
    void startMapType(String name, DMapType type) throws Exception;
    void structMember(String name, DType type) throws Exception;
    void rule(int index, String ruleText, NRule rule) throws Exception;
    void enumMember(String name, DType memberType) throws Exception;
    void endType(String name, DType type) throws Exception;

    void value(String name, DValue dval, DValue parentVal) throws Exception;
    void startStruct(String name, DValue dval) throws Exception;
    void startList(String name, DValue value) throws Exception;
    void startMap(String name, DValue value) throws Exception;
    void endStruct(String name, DValue value) throws Exception;
    void endList(String name, DValue value) throws Exception;
    void endMap(String name, DValue value) throws Exception;
    
    void finish() throws Exception;
}
