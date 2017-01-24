package org.dval.nrule.virtual;

import org.dval.DValue;
import org.dval.nrule.NRuleContext;

public interface VirtualDataItem {
    void resolve(DValue dval, NRuleContext ctx);
    
}