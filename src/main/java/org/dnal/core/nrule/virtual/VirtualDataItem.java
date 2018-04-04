package org.dnal.core.nrule.virtual;

import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;

public interface VirtualDataItem {
    void resolve(DValue dval, NRuleContext ctx);
    Shape getTargetShape();
    boolean containsValue(DValue dval); //will be false for optional
}