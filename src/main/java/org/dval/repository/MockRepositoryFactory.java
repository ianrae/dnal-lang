package org.dval.repository;

import org.dval.DType;

public class MockRepositoryFactory implements RepositoryFactory {

	@Override
	public Repository createFor(DType type) {
		return new MockRepository(type);
	}

}
