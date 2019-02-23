package org.dnal.core.nrule.virtual;

import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;

public class VirtualDateMember extends VirtualDate implements StructMember {
    public String fieldName;


    @Override
    public void resolve(DValue dval, NRuleContext ctx) {
        DStructHelper helper = dval.asStruct();
        DValue tmp = helper.getField(fieldName);
        if (tmp != null) {
        	super.resolve(tmp, ctx);
        }
    }
    
    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
	@Override
	public boolean containsValue(DValue dval) {
        DStructHelper helper = dval.asStruct();
        DValue tmp = helper.getField(fieldName);
        return (tmp != null);
	}

    @Override
    public String getFieldName() {
        return fieldName;
    }
}