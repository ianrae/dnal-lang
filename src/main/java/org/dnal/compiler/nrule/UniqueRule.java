package org.dnal.compiler.nrule;

import org.dnal.api.impl.CompilerContext;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleBase;
import org.dnal.core.nrule.NRuleContext;

public class UniqueRule extends NRuleBase {
    
    private String fieldName;
    private CompilerContext context;

    public UniqueRule(String name, String fieldName, DType type, CompilerContext context) {
        super(name);
        this.fieldName = fieldName;
        this.context = context;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public boolean eval(DValue dval, NRuleContext ctx) {
        DStructType structType = (DStructType) dval.getType();
        DType innerType = structType.getFields().get(fieldName);
        if (innerType == null) {
            this.addRuleFailedError(ctx, this.getRuleText());
            return false;
        }
        
        boolean pass = false;
        switch(innerType.getShape()) {
        case INTEGER:
        case LONG:
        case STRING:
            pass = checkRule(structType);
            break;
        default:
            this.addRuleFailedError(ctx, this.getRuleText() + " - can only be used on fields of type int,long, or string");
            break;
        }
        
        return pass;
    }
    

    private boolean checkRule(DStructType structType) {
        //search all values of structType (and child-types) 
        //build map of values. error if any duplicates
        return false;
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        return true;
    }
}