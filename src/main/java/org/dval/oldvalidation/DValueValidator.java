package org.dval.oldvalidation;

import org.dval.DValue;

public interface DValueValidator {

	void evaluate(DValue dval, RuleContext ctx);
}
