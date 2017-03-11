package org.dnal.core.nrule.virtual;

import java.util.Date;

import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;

public class VirtualDate implements VirtualDataItem, Comparable<Date> {
    public Date val;

    @Override
    public int compareTo(Date arg0) {
        return val.compareTo(arg0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VirtualDate) {
            VirtualDate vi = (VirtualDate) obj;
            return vi.val.equals(val);
        } else if (obj instanceof Date) {
            Date n = (Date) obj;
            return val.equals(n);
        }
        return false;
    }

    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        val = dval.asDate();
    }

    @Override
    public Shape getTargetShape() {
        return Shape.DATE;
    }
    
	@Override
	public String toString() {
		return val.toString();
	}
}