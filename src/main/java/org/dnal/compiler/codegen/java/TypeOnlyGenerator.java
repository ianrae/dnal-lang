package org.dnal.compiler.codegen.java;

import org.dnal.compiler.generate.GenerateVisitor;
import org.dnal.core.DListType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRule;

public abstract class TypeOnlyGenerator implements GenerateVisitor {
    
    public void startType(String name, DType dtype) throws Exception {}
    public void startListType(String name, DListType type) throws Exception {}
    public void endType(String name, DType type) throws Exception {}
    public void structMember(String name, DType type) throws Exception {}
    public void endMember(String name, DType type) throws Exception{}
    public void rule(String ruleText, NRule rule) throws Exception{}
    public void enumMember(String name, DType memberType) throws Exception{}

    public void value(String name, DValue dval, DValue parentVal) throws Exception{}
    public void startStruct(String name, DValue dval) throws Exception{}
    public void startList(String name, DValue value) throws Exception{}
    public void endStruct(String name, DValue value) throws Exception{}
    public void endList(String name, DValue value) throws Exception{}
    public void startEnum(String name, DValue dval) throws Exception{}
    public void endEnum(String name, DValue dval) throws Exception{}
    
    public void finish() throws Exception{}

}