package com.github.ianrae.dnalparse.codegen.java;

import org.dval.DListType;
import org.dval.DType;
import org.dval.Shape;
import org.dval.logger.Log;
import org.dval.nrule.NRule;

import com.github.ianrae.dnalc.ConfigFileOptions;
import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.FullListTypeExp;
import com.github.ianrae.dnalparse.parser.ast.FullTypeExp;
import com.github.ianrae.dnalparse.parser.ast.StructMemberExp;
import com.github.ianrae.dnalparse.parser.error.TypeInfo;

public class ImmutableBeanCodeGen extends CodeGenBase {

    public ImmutableBeanCodeGen(ConfigFileOptions options) {
        super(options);
    }

    @Override
    public void startType(String name, DType dtype) {
        onStartType(name, dtype);
        
        String baseTypeName = TypeInfo.getBaseTypeName(dtype);
//        String s = String.format("type:%s:%s", name, baseTypeName);
        if (dtype.isScalarShape()) {
            String s = st.genScalarImmutableBean(options.javaPackage, name, baseTypeName, dtype.getShape());
            outputL.add(s);
        } else if (dtype.isShape(Shape.STRUCT)) {
            String s = st.genScalarImmutableBean(options.javaPackage, name, baseTypeName);
            outputL.add(s);
        } else {
            Log.log("errrrrrrrrrrrrrrrr!");
        }
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
    public void startMember(String name, DType type) {
        DType membType = type;
        String s = st.generateImmutableBeanMember(name, membType.getShape());
        outputL.add(s);
    }

    @Override
    public void endMember(String name, DType s) {
    }

    @Override
    public void rule(String ruleText, NRule rule) {
        String s = String.format(" r: %s", ruleText);
        outputL.add(s);
    }

    @Override
    public void finish() {
        for(String s: outputL) {
            Log.log(s);
        }
    }
}