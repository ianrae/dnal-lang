package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.CustomRule;

public interface NeedsCustomRule {
	void rememberCustomRule(CustomRule exp);

}
