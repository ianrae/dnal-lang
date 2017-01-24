package org.dval;

import static org.junit.Assert.*;

import org.dval.repository.MockRepository;
import org.dval.repository.MockRepositoryFactory;
import org.dval.repository.MyWorld;
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

		MyWorld world = new MyWorld();
		world.setRepositoryFactory(new MockRepositoryFactory());
		world.typeRegistered(type);
		DValue dval = buildIntVal(registry, 45);
		world.valueAdded(dval);

		assertEquals(true, world.inRepo(dval));
	}
	
	@Test
	public void test3() {
		DType type = registry.getType(BuiltInTypes.ENUM_SHAPE);

		MyWorld world = new MyWorld();
		world.setRepositoryFactory(new MockRepositoryFactory());
		world.typeRegistered(type);
		DValue dval = buildEnumVal(registry, "RED");
		world.valueAdded(dval);

		assertEquals(true, world.inRepo(dval));
	}
}
