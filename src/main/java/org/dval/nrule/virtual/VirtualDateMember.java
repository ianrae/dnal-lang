package org.dval.nrule.virtual;

import org.dval.DStructHelper;
import org.dval.DValue;
import org.dval.nrule.NRuleContext;

public class VirtualDateMember extends VirtualDate implements StructMember {
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