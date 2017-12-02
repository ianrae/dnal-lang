package org.dnal.compiler.codegen.java;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.generate.OutputGenerator;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRule;
import org.dnal.dnalc.ConfigFileOptions;

public class JavaCodeGen extends CodeGenBase {
    public List<OutputGenerator> list = new ArrayList<>();
    
    public JavaCodeGen() {
        list.add(new InterfaceCodeGen());
        list.add(new ImmutableBeanCodeGen());
        list.add(new BeanCodeGen());
    }
    
    @Override
    public void startStructType(String name, DStructType dtype) throws Exception {
        for(OutputGenerator visitor: list) {
            visitor.startStructType(name, dtype);
        }
    }
    @Override
    public void startEnumType(String name, DStructType dtype) throws Exception {
        for(OutputGenerator visitor: list) {
            visitor.startEnumType(name, dtype);
        }
    }

    @Override
    public void startType(String name, DType dtype) throws Exception {
        for(OutputGenerator visitor: list) {
            visitor.startType(name, dtype);
        }
    }

    @Override
    public void startListType(String name, DListType type) throws Exception {
        for(OutputGenerator visitor: list) {
            visitor.startListType(name, type);
        }
    }


    @Override
    public void endType(String name, DType type) throws Exception {
        for(OutputGenerator visitor: list) {
            visitor.endType(name, type);
        }
    }

    @Override
    public void structMember(String name, DType type) throws Exception {
        for(OutputGenerator visitor: list) {
            visitor.structMember(name, type);
        }
    }

    @Override
    public void rule(int index, String ruleText, NRule rule) throws Exception {
        for(OutputGenerator visitor: list) {
            visitor.rule(index, ruleText, rule);
        }
    }

    @Override
    public void finish() throws Exception {
        for(OutputGenerator visitor: list) {
            visitor.finish();
        }
    }

	@Override
	public void setOptions(ConfigFileOptions configFileOptions) {
        for(OutputGenerator visitor: list) {
            visitor.setOptions(configFileOptions);
        }
	}

	@Override
	public void startMapType(String name, DMapType type) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startMap(String name, DValue value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endMap(String name, DValue value) throws Exception {
		// TODO Auto-generated method stub
		
	}
}