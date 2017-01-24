package org.dnal.compiler.nrule;

import java.util.List;

import org.dnal.core.nrule.NRuleBase;

public abstract class CustomMultiRule<T> extends NRuleBase {
	protected List<T> argL;
	
	public CustomMultiRule(String name, List<T> argL) {
		super(name);
		this.argL = argL;
	}
}