package org.dnal.compiler.codegen.java;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullListTypeExp;
import org.dnal.compiler.parser.ast.FullTypeExp;
import org.dnal.compiler.parser.ast.StructMemberExp;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DType;
import org.dnal.core.Shape;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRule;
import org.dnal.dnalc.ConfigFileOptions;

public class BeanCodeGen extends CodeGenBase {
    
    public BeanCodeGen(ConfigFileOptions options) {
        super(options);
    }

    @Override
    public void startType(String name, DType dtype) {
        onStartType(name, dtype);
        
        String baseTypeName = TypeInfo.getBaseTypeName(dtype);
//        String s = String.format("type:%s:%s", name, baseTypeName);
        if (dtype.isScalarShape()) {
            String s = st.genScalarBean(options.javaPackage, name, baseTypeName, dtype.getShape());
            outputL.add(s);
        } else if (dtype.isShape(Shape.STRUCT)) {
            String s = st.genStructBean(options.javaPackage, name, baseTypeName);
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
//        onEndType(name, type, "Bean");
        onEndType(name, "Bean");
    }

    @Override
    public void startMember(String name, DType type) {
        DType membType = type;
        String s = st.generateBeanMemberDecl(name, membType.getShape());
        outputL.add(s);
        s = st.generateBeanMember(name, membType.getShape());
        outputL.add(s);
    }

    @Override
    public void endMember(String name, DType type) {
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