package org.dnal.core.oldvalidation;

import org.dnal.core.DValue;

public interface DValueValidator {

	void evaluate(DValue dval, RuleContext ctx);
}
