package com.github.ianrae.dnalparse.codegen.java;

import org.dval.Shape;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class JavaST {
    private static final String CODEGEN_DIR = "./src/main/resources/codegen/";
    
    private ST getInterfaceST(String name) {
        final STGroup stGroup = new STGroupFile(CODEGEN_DIR + "interfaceScalar.stg");
        final ST templateExample = stGroup.getInstanceOf(name);
        return templateExample;
    }
    private ST getImmuteST(String name) {
        final STGroup stGroup = new STGroupFile(CODEGEN_DIR + "beanImmuteScalar.stg");
        final ST templateExample = stGroup.getInstanceOf(name);
        return templateExample;
    }
    private ST getBeanST(String name) {
        final STGroup stGroup = new STGroupFile(CODEGEN_DIR + "beanScalar.stg");
        final ST templateExample = stGroup.getInstanceOf(name);
        return templateExample;
    }

    public String genScalarInterface(String pkg, String name, String baseTypeName, Shape shape) {
        final ST templateExample = getInterfaceST("classTemplate");

        // Pass on values to use when rendering
        templateExample.add("pkg", pkg);
        templateExample.add("className", name);
        templateExample.add("baseClassName", baseTypeName);
        templateExample.add("shapeType", getShapeAsType(shape));

        final String render = templateExample.render();
        return render;
    }
    
    public String genScalarImmutableBean(String pkg, String name, String baseTypeName, Shape shape) {
        final ST templateExample = getImmuteST("classTemplate");

        // Pass on values to use when rendering
        templateExample.add("pkg", pkg);
        templateExample.add("className", name);
        templateExample.add("baseClassName", baseTypeName);
        templateExample.add("shapeType", getShapeAsType(shape));
        templateExample.add("dvmethod", getDValueMethod(shape));

        final String render = templateExample.render();
        return render;
    }
    
    public String genScalarBean(String pkg, String name, String baseTypeName, Shape shape) {
        final ST templateExample = getBeanST("classTemplate");

        // Pass on values to use when rendering
        templateExample.add("pkg", pkg);
        templateExample.add("className", name);
        templateExample.add("baseClassName", baseTypeName);
        templateExample.add("shapeType", getShapeAsType(shape));

        final String render = templateExample.render();
        return render;
    }

    private String getShapeAsType(Shape shape) {
        String type = null;
        switch(shape) {
        case INTEGER: 
            type = "int";
            break;
        case NUMBER: 
            type = "double";
            break;
        case DATE: 
            type = "Date";
            break;
        case BOOLEAN:
            type = "boolean";
            break;
        case STRING:
            type = "String";
            break;
//        case LIST,
//        case REF,
//        case STRUCT,
//        case ENUM
        default:
            break;
        }
        return type;
    }
    private String getDValueMethod(Shape shape) {
        String type = null;
        switch(shape) {
        case INTEGER: 
            type = "asInt";
            break;
        case NUMBER: 
            type = "asNumber";
            break;
        case DATE: 
            type = "asDate";
            break;
        case BOOLEAN:
            type = "asBoolean";
            break;
        case STRING:
            type = "asString";
            break;
//        case LIST,
//        case REF,
//        case STRUCT,
//        case ENUM
        default:
            break;
        }
        return type;
    }
    

    public String generateStructInterface(String pkg, String name, String baseTypeName) {
        final ST templateExample = getInterfaceST("structTemplate");

        // Pass on values to use when rendering
        templateExample.add("pkg", pkg);
        templateExample.add("className", name);
        templateExample.add("baseClassName", baseTypeName);

        final String render = templateExample.render();
        return render;
    }

    public String genStructBean(String pkg, String name, String baseTypeName) {
        final ST templateExample = getBeanST("structTemplate");

        // Pass on values to use when rendering
        templateExample.add("pkg", pkg);
        templateExample.add("className", name);
        templateExample.add("baseClassName", baseTypeName);

        final String render = templateExample.render();
        return render;
    }

    public String genScalarImmutableBean(String pkg, String name, String baseTypeName) {
        final ST templateExample = getImmuteST("structTemplate");

        // Pass on values to use when rendering
        templateExample.add("pkg", pkg);
        templateExample.add("className", name);
        templateExample.add("baseClassName", baseTypeName);

        final String render = templateExample.render();
        return render;
    }

    public String generateInterfaceMember(String name, Shape shape) {
        final ST templateExample = getInterfaceST("memberTemplate");

        // Pass on values to use when rendering
        templateExample.add("fieldName", uppify(name));
        templateExample.add("shapeType", getShapeAsType(shape));

        final String render = templateExample.render();
        return render;
    }
    
    public String uppify(String name) {
        if (name.length() <= 1) {
            return name.toUpperCase();
        } else {
            String first = name.substring(0, 1).toUpperCase();
            return first + name.substring(1);
        }
    }
    public String generateImmutableBeanMember(String name, Shape shape) {
        final ST templateExample = getImmuteST("memberTemplate");

        // Pass on values to use when rendering
        templateExample.add("ufieldName", uppify(name));
        templateExample.add("fieldName", name);
        templateExample.add("shapeType", getShapeAsType(shape));
        
        String fn = "as" + uppify(getShapeAsType(shape));
        templateExample.add("dvalFn", fn);

        final String render = templateExample.render();
        return render;
    }
    public String generateBeanMemberDecl(String name, Shape shape) {
        final ST templateExample = getBeanST("memberDeclTemplate");

        // Pass on values to use when rendering
        templateExample.add("ufieldName", uppify(name));
        templateExample.add("fieldName", name);
        templateExample.add("shapeType", getShapeAsType(shape));
        
        final String render = templateExample.render();
        return render;
    }
    public String generateBeanMember(String name, Shape shape) {
        final ST templateExample = getBeanST("memberTemplate");

        // Pass on values to use when rendering
        templateExample.add("ufieldName", uppify(name));
        templateExample.add("fieldName", name);
        templateExample.add("shapeType", getShapeAsType(shape));
        
        final String render = templateExample.render();
        return render;
    }

}
