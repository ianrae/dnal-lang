package org.dval.nrule.virtual;

import java.util.Date;

import org.dval.DValue;
import org.dval.nrule.NRuleContext;

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
}