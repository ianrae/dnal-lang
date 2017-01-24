package com.github.ianrae.dnalparse.nrule;

import org.dval.DStructType;
import org.dval.DType;
import org.dval.DValue;
import org.dval.nrule.NRuleBase;
import org.dval.nrule.NRuleContext;

import com.github.ianrae.dnalparse.impl.CompilerContext;

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