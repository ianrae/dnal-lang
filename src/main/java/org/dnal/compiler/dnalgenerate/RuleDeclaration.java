package org.dnal.compiler.dnalgenerate;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.Shape;

public class RuleDeclaration {
    public String ruleName;
    public List<Shape> shapeL = new ArrayList<>();

    public RuleDeclaration(String name) {
        this.ruleName = name;
    }
    public RuleDeclaration(String name, Shape shape) {
        this.ruleName = name;
        this.shapeL.add(shape);
    }
}
