package org.dnal.core;

import static org.junit.Assert.*;

import org.dnal.core.BuiltInTypes;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.repository.MockRepository;
import org.dnal.core.repository.MockRepositoryFactory;
import org.dnal.core.repository.World;
import org.junit.Test;

public class RepositoryTests  extends BaseDValTest {
	@Test
	public void test() {
		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);

		MockRepository repo = new MockRepository(type);
		assertEquals(0, repo.size());

		DValue dval = buildIntVal(registry, 45);
		repo.add(dval);
		assertEquals(1, repo.size());
	}

	@Test
	public void test2() {
		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);

		World world = new World();
		world.setRepositoryFactory(new MockRepositoryFactory());
		world.typeRegistered(type);
		DValue dval = buildIntVal(registry, 45);
		world.valueAdded(dval);

		assertEquals(true, world.inRepo(dval));
	}
	
	@Test
	public void test3() {
		DStructType type = buildColourEnumType(registry);

		World world = new World();
		world.setRepositoryFactory(new MockRepositoryFactory());
		world.typeRegistered(type);
		DValue dval = buildEnumVal(registry, type, "RED");
		world.valueAdded(dval);

		assertEquals(true, world.inRepo(dval));
	}
}
