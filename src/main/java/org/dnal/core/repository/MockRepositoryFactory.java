package org.dnal.core.repository;

import org.dnal.core.DType;

public class MockRepositoryFactory implements RepositoryFactory {

	@Override
	public Repository createFor(DType type) {
		return new MockRepository(type);
	}

}
