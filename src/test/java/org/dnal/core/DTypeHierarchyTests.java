package org.dnal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.dnal.core.BuiltInTypes;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeHierarchy;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.fluent.type.TypeBuilder;
import org.junit.Test;

public class DTypeHierarchyTests extends BaseDValTest {

	@Test
	public void test() {
		TypeBuilder tb = new TypeBuilder(registry, world);
		tb.start("Person")
		.stringList("name")
//		.reference("region", buildAddressType(registry), "code")
		.end();

		DStructType type = tb.getType();
		assertEquals("Person", type.getName());
		assertEquals(1, type.getFields().size());
		
        tb = new TypeBuilder(registry, world);
        tb.start("Address")
        .stringList("code")
        .end();
		
		world.dumpType(type);
		world.dump();
		registry.dump();
		
		DTypeHierarchy th = registry.getHierarchy();
		DType other = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
		assertNotNull(other);
		assertEquals(false, th.isParent(type, other));
		assertEquals(false, th.isParent(type, type));
		assertEquals(false, th.isParent(other, type));
		
		other = registry.getType("Address");
		assertNotNull(other);
        assertEquals(false, th.isParent(type, other));
        
        tb = new TypeBuilder(registry, world);
        tb.setBaseType(type);
        tb.start("Child")
        .stringList("game")
        .end();
        
        th = registry.getHierarchy();
        other = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
        assertNotNull(other);
        assertEquals(false, th.isParent(type, other));
        assertEquals(false, th.isParent(type, type));
        assertEquals(false, th.isParent(other, type));
        
        other = registry.getType("Child");
        assertNotNull(other);
        assertEquals(false, th.isParent(type, other));
        assertEquals(true, th.isParent(other, type));
        
        assertEquals(true, th.isChild(type, other));
        assertEquals(false, th.isChild(other, type));
        
        List<DType> list = registry.getChildTypes(type);
        assertEquals(1, list.size());
        assertSame(other, list.get(0));
        
        list = registry.getParentTypes(other);
        assertEquals(1, list.size());
        assertSame(type, list.get(0));
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
