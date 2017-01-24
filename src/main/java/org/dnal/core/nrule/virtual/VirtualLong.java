package org.dnal.core.nrule.virtual;

import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;

public class VirtualLong implements VirtualDataItem, Comparable<Long> {
    public Long val;
    public boolean isDateLong;

    @Override
    public int compareTo(Long arg0) {
        return val.compareTo(arg0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VirtualLong) {
            VirtualLong vi = (VirtualLong) obj;
            return vi.val.equals(val);
        } else if (obj instanceof Long) {
            Long n = (Long) obj;
            return val.equals(n);
        }
        return false;
    }

    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        if (isDateLong) {
            val = dval.asDate().getTime();
        } else {
            val = dval.asLong();
        }
    }
}