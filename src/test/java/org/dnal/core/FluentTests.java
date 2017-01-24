package org.dnal.core;

import static org.junit.Assert.*;

import org.dnal.core.DStructType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.fluent.type.TypeBuilder;
import org.junit.Test;

public class FluentTests extends BaseDValTest {

	@Test
	public void test() {
		TypeBuilder tb = new TypeBuilder(registry, world);
		tb.start("Person")
		.string("name").notEmpty().minSize(35)
		.string("lastName")
		.integer("age").min(100)
		.longInteger("dbid")
		.number("weight").min(10.0)
		.bool("flag")
		.enumeration("color")
		.date("when")
		.stringList("nickNames")
		.end();

		DStructType type = tb.getType();
		assertEquals("Person", type.getName());
		assertEquals(9, type.getFields().size());
		world.dumpType(type);
		world.dump();
		registry.dump();
	}

	@Test
	public void testNoRegistry() {
		TypeBuilder tb = new TypeBuilder();
		tb.start("Person")
		.string("name")
		.string("lastName")
		.integer("age")
		.end();

		DStructType type = tb.getType();
		assertEquals("Person", type.getName());
		assertEquals(3, type.getFields().size());
	}

	//--
	private DStructType buildAddressType(DTypeRegistry registry) {
		DStructType type = (DStructType) registry.getType("Address");
		if (type != null) {
			return type;
		}
		
		TypeBuilder tb = new TypeBuilder(registry, world);
		tb.start("Address")
		.string("street").notEmpty()
		.string("city")
		.end();

		type = tb.getType();
		registerType("Address", type);
		return type;
	}
}
