package org.dnal.core.repository;

import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;

public interface Repository {
	DType getType();
	List<DValue> getAll();
	void add(DValue dval);
	long size();
	boolean inRepo(DValue dval);
//	DValue findByField(String fieldName, Object value);
//	void scoreRepository(ValidationScorer scorer);
}