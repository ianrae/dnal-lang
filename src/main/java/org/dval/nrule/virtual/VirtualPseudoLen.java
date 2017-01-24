package org.dval.nrule.virtual;

import org.dval.DValue;
import org.dval.Shape;
import org.dval.ErrorMessage;
import org.dval.ErrorType;
import org.dval.nrule.NRuleContext;

public class VirtualPseudoLen extends VirtualInt {
    
    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        if (dval.getType().isShape(Shape.STRING)) {
            val = dval.asString().length();
        } else if (dval.getType().isShape(Shape.LIST)) {
            val = dval.asList().size();
        } else {
            ErrorMessage valerr = new ErrorMessage(ErrorType.INVALIDRULE, "len only works on string and list");
            ctx.addError(valerr);
        }
    }
    
}