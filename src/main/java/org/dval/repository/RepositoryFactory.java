package org.dval.repository;

import org.dval.DType;

public interface RepositoryFactory {
	Repository createFor(DType type);
}
