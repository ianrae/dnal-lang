package org.dnal.core.repository;

import java.util.Map;

import org.dnal.core.DType;
import org.dnal.core.DValue;

public interface WorldListener extends WorldAdder {
	void typeRegistered(DType type);
	boolean hasRepo(DType dtype);
	boolean inRepo(DValue dval);
	void setRepositoryFactory(RepositoryFactory factory);
	Repository getRepoFor(DType dtype);
	Map<DType, Repository> getRepoMap();
}