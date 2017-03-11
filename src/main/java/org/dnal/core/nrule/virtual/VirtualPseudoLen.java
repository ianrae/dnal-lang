package org.dnal.core.nrule.virtual;

import org.dnal.core.DValue;
import org.dnal.core.ErrorType;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;

public class VirtualPseudoLen extends VirtualInt {
    
    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        if (dval.getType().isShape(Shape.STRING)) {
            val = dval.asString().length();
        } else if (dval.getType().isShape(Shape.LIST)) {
            val = dval.asList().size();
        } else {
        	ctx.addError(ErrorType.INVALIDRULE, "len only works on string and list");
        }
    }
    
}