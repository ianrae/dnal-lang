package com.github.ianrae.dnalparse.generate;

import org.dnal.core.DListType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRule;

public interface GenerateVisitor {
    void startType(String name, DType dtype) throws Exception;
    void startListType(String name, DListType type) throws Exception;
    void endType(String name, DType type) throws Exception;
    void startMember(String name, DType type) throws Exception;
    void endMember(String name, DType type) throws Exception;
    void rule(String ruleText, NRule rule) throws Exception;
    void enumMember(String name, DType memberType) throws Exception;

    void value(String name, DValue dval) throws Exception;
    void startStruct(String name, DValue dval) throws Exception;
    void startList(String name, DValue value) throws Exception;
    void endStruct(String name, DValue value) throws Exception;
    void endList(String name, DValue value) throws Exception;
    
    void finish() throws Exception;
}
