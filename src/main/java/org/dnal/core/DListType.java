package org.dnal.core;

public class DListType extends DType {
	private DType elementType;

	public DListType(Shape shape, String name, DType baseType, DType elementType) {
		super(shape, name, baseType);
		this.elementType = elementType;
	}

	public DType getElementType() {
		return elementType;
	}
}