package org.dnal.api.bean;

import org.dnal.api.Transaction;
import org.dnal.core.DType;
import org.dnal.core.DValue;

public interface FieldConverter {
	DValue convertToDVal(Transaction trans, DType type, Object value);
}
