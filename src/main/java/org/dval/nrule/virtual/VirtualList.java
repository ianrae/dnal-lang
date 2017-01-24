package org.dval.nrule.virtual;

import java.util.List;

import org.dval.DValue;
import org.dval.nrule.NRuleContext;

public class VirtualList implements VirtualDataItem {
	public List<DValue> val;

    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        val = dval.asList();
    }

}