package org.dnal.core.nrule.virtual;

import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;

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
	@Override
	public boolean containsValue(DValue dval) {
		return true;
	}

    @Override
    public boolean equals(Object obj) {
        return val.equals(obj);
    }

    @Override
    public Shape getTargetShape() {
        return Shape.STRING;
    }
    
	@Override
	public String toString() {
		return val.toString();
	}
}