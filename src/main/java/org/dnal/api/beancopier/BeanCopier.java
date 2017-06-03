package org.dnal.api.beancopier;

import java.util.List;

import org.dnal.core.NewErrorMessage;

public interface BeanCopier {

	boolean copy(Object sourceObj, Object destObj, List<FieldSpec> fieldL);
	List<NewErrorMessage> getErrors();
	void dumpErrors();
	void dumpPeformanceInfo();
}