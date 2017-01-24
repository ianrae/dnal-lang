package com.github.ianrae.dnalparse.codegen.java;

import java.util.ArrayList;
import java.util.List;

import org.dval.DType;
import org.dval.Shape;


public class ImportObserver {
    public List<String> importL = new ArrayList<>();
    
    public void addImport(DType dtype) {
        if (dtype.isScalarShape()) {
            String s = shapeToImport(dtype.getShape());
            if (s != null) {
                importL.add(s);
            }
        } 
    }
    
    private String shapeToImport(Shape shape) {
        switch(shape) {
        case STRING: //hack!!
            return "java.util.XString";
        case DATE:
            return "java.util.Date";
        case LIST:
            return "java.util.List";
        default:
            return null;
        }
    }
}
