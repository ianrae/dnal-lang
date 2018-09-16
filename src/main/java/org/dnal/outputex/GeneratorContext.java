package org.dnal.outputex;

import java.util.Stack;

public class GeneratorContext {
	public final static String LIST = "L";
	public final static String STRUCT = "S";
	public final static String MAP = "M";
	
    private Stack<String> shapeStack = new Stack<>();

    public void pushShapeCode(String shape) {
    	shapeStack.push(shape);
    }
    public void popShapeCode() {
    	shapeStack.pop();
    }
    public String getCurrentShapeCode() {
        String shape = (shapeStack.isEmpty()) ? "" : shapeStack.peek();
        return shape;
    }
}