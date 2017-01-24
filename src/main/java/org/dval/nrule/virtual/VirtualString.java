package org.dval.nrule.virtual;

import org.dval.DValue;
import org.dval.nrule.NRuleContext;

public class VirtualString implements VirtualDataItem, Comparable<String> {
	public String val;

	@Override
	public int compareTo(String arg0) {
		return val.compareTo(arg0);
	}

    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        val = dval.asString();
    }
}