package org.dnal.core.nrule.virtual;

import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;


public class VirtualPseudoLenMember extends VirtualPseudoLen implements StructMember {
    
    public String fieldName;

    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        DStructHelper helper = new DStructHelper(dval);
        DValue tmp = helper.getField(fieldName);
        super.resolve(tmp, ctx);
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}