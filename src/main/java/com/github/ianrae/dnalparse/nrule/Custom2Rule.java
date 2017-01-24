package com.github.ianrae.dnalparse.nrule;

import org.dnal.core.nrule.NRuleBase;

public abstract class Custom2Rule<T> extends NRuleBase {
	protected T arg1;
	protected T arg2;
	
	public Custom2Rule(String name, T arg1, T arg2) {
		super(name);
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
}