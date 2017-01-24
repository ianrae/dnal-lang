package org.dnal.core;

import org.dnal.core.repository.WorldListener;

public class DTypeRegistryBuilder {
	private DTypeRegistry registry = new DTypeRegistry();
	private WorldListener listener;

	public void init(WorldListener listener) {
	    this.listener = listener;
		
		String name = BuiltInTypes.INTEGER_SHAPE.name();
		DType type = new DType(Shape.INTEGER, name, null);
        registerType(name, type);

        name = BuiltInTypes.LONG_SHAPE.name();
        type = new DType(Shape.LONG, name, null);
        registerType(name, type);

        name = BuiltInTypes.NUMBER_SHAPE.name();
        type = new DType(Shape.NUMBER, name, null);
        registerType(name, type);

		name = BuiltInTypes.STRING_SHAPE.name();
		type = new DType(Shape.STRING, name, null);
        registerType(name, type);

		name = BuiltInTypes.BOOLEAN_SHAPE.name();
		type = new DType(Shape.BOOLEAN, name, null);
        registerType(name, type);

		name = BuiltInTypes.DATE_SHAPE.name();
		type = new DType(Shape.DATE, name, null);
        registerType(name, type);

		name = BuiltInTypes.ENUM_SHAPE.name();
		type = new DType(Shape.ENUM, name, null);
		registerType(name, type);
	}
	
    private void registerType(String typeName, DType dtype) {
        registry.add(typeName, dtype);
        listener.typeRegistered(dtype);
    }

	public DTypeRegistry getRegistry() {
		return registry;
	}
}