package org.dnal.core.repository;

import org.dnal.core.DType;

public interface RepositoryFactory {
	Repository createFor(DType type);
}
