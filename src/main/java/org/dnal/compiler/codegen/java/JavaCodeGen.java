package org.dnal.compiler.codegen.java;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.generate.OuputGenerator;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.nrule.NRule;
import org.dnal.dnalc.ConfigFileOptions;

public class JavaCodeGen extends CodeGenBase {
    public List<OuputGenerator> list = new ArrayList<>();
    
    public JavaCodeGen() {
        list.add(new InterfaceCodeGen());
        list.add(new ImmutableBeanCodeGen());
        list.add(new BeanCodeGen());
    }
    
    @Override
    public void startStructType(String name, DStructType dtype) throws Exception {
        for(OuputGenerator visitor: list) {
            visitor.startStructType(name, dtype);
        }
    }
    @Override
    public void startEnumType(String name, DStructType dtype) throws Exception {
        for(OuputGenerator visitor: list) {
            visitor.startEnumType(name, dtype);
        }
    }

    @Override
    public void startType(String name, DType dtype) throws Exception {
        for(OuputGenerator visitor: list) {
            visitor.startType(name, dtype);
        }
    }

    @Override
    public void startListType(String name, DListType type) throws Exception {
        for(OuputGenerator visitor: list) {
            visitor.startListType(name, type);
        }
    }


    @Override
    public void endType(String name, DType type) throws Exception {
        for(OuputGenerator visitor: list) {
            visitor.endType(name, type);
        }
    }

    @Override
    public void structMember(String name, DType type) throws Exception {
        for(OuputGenerator visitor: list) {
            visitor.structMember(name, type);
        }
    }

    @Override
    public void rule(int index, String ruleText, NRule rule) throws Exception {
        for(OuputGenerator visitor: list) {
            visitor.rule(index, ruleText, rule);
        }
    }

    @Override
    public void finish() throws Exception {
        for(OuputGenerator visitor: list) {
            visitor.finish();
        }
    }

	@Override
	public void setOptions(ConfigFileOptions configFileOptions) {
        for(OuputGenerator visitor: list) {
            visitor.setOptions(configFileOptions);
        }
	}
}