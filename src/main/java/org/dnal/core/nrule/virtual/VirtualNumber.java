package org.dnal.core.nrule.virtual;

import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;

public class VirtualNumber implements VirtualDataItem, Comparable<Double> {
    public Double val;

    @Override
    public int compareTo(Double arg0) {
        return val.compareTo(arg0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VirtualNumber) {
            VirtualNumber vi = (VirtualNumber) obj;
            return vi.val.equals(val);
        } else if (obj instanceof Double) {
            Double n = (Double) obj;
            return val.equals(n);
        }
        return false;
    }

    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        val = dval.asNumber();
    }
    
    @Override
    public Shape getTargetShape() {
        return Shape.NUMBER;
    }
}