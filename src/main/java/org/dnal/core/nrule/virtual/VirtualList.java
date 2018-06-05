package org.dnal.core.nrule.virtual;

import java.util.List;

import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;

public class VirtualList implements VirtualDataItem {
	public List<DValue> val;

    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        val = dval.asList();
    }
	@Override
	public boolean containsValue(DValue dval) {
		return true;
	}

    @Override
    public Shape getTargetShape() {
        return Shape.LIST;
    }

}