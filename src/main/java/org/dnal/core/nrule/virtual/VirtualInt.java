package org.dnal.core.nrule.virtual;

import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;

public class VirtualInt implements VirtualDataItem, Comparable<Integer> {
	public Integer val;

	@Override
	public int compareTo(Integer arg0) {
		return val.compareTo(arg0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VirtualInt) {
			VirtualInt vi = (VirtualInt) obj;
			return vi.val.equals(val);
		} else if (obj instanceof Integer) {
			Integer n = (Integer) obj;
			return val.equals(n);
		}
		return false;
	}

    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        val = dval.asInt();
    }
	@Override
	public boolean containsValue(DValue dval) {
		return true;
	}
    

    @Override
    public Shape getTargetShape() {
        return Shape.INTEGER;
    }

	@Override
	public String toString() {
		return val.toString();
	}
}