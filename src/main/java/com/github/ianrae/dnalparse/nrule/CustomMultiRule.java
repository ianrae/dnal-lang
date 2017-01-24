package com.github.ianrae.dnalparse.nrule;

import java.util.List;

import org.dval.nrule.NRuleBase;

public abstract class CustomMultiRule<T> extends NRuleBase {
	protected List<T> argL;
	
	public CustomMultiRule(String name, List<T> argL) {
		super(name);
		this.argL = argL;
	}
}