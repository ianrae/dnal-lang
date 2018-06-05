package org.dnal.compiler.codegen.java;

import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRule;
import org.dnal.dnalc.ConfigFileOptions;

public class ImmutableBeanCodeGen extends CodeGenBase {

    @Override
    public void startStructType(String name, DStructType dtype) {
        onStartType(name, dtype);

        String baseTypeName = TypeInfo.getBaseTypeName(dtype);
        String s = st.genScalarImmutableBean(options.javaPackage, name, baseTypeName);
        outputL.add(s);
    }
    @Override
    public void startEnumType(String name, DStructType dtype) {
        onStartType(name, dtype);

        String baseTypeName = TypeInfo.getBaseTypeName(dtype);
        String s = st.genScalarImmutableBean(options.javaPackage, name, baseTypeName);
        outputL.add(s);
    }
    @Override
    public void startType(String name, DType dtype) {
        onStartType(name, dtype);

        String baseTypeName = TypeInfo.getBaseTypeName(dtype);
        //        String s = String.format("type:%s:%s", name, baseTypeName);
        String s = st.genScalarImmutableBean(options.javaPackage, name, baseTypeName, dtype.getShape());
        outputL.add(s);
    }


    @Override
    public void startListType(String name, DListType type) {
        String elType = type.getElementType().getName();
        String baseTypeName = (type.getBaseType() == null) ? String.format("list<%s>", elType) : type.getName();
        String s = String.format("type:%s:%s", name, baseTypeName);
        outputL.add(s);
    }


    @Override
    public void endType(String name, DType type) {
        onEndType(name, "Data");
    }

    @Override
    public void structMember(String name, DType type) {
        DType membType = type;
        String s = st.generateImmutableBeanMember(name, membType.getShape());
        outputL.add(s);
    }

    @Override
    public void rule(int index, String ruleText, NRule rule) {
        String s = String.format(" r: %s", ruleText);
        outputL.add(s);
    }

    @Override
    public void finish() {
        for(String s: outputL) {
            Log.log(s);
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