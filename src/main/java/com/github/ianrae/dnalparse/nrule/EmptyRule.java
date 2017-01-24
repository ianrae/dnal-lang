package com.github.ianrae.dnalparse.nrule;

import org.dval.DStructHelper;
import org.dval.DValue;
import org.dval.Shape;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.virtual.VirtualString;

import com.github.ianrae.dnalparse.parser.ast.CustomRule;


public class EmptyRule extends Custom1Rule<VirtualString> implements NeedsCustomRule { 
    public CustomRule crule;
    
    public EmptyRule(String name, VirtualString arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        DValue tmp = dval;
        
        if (dval.getType().isShape(Shape.STRUCT)) {
            String fieldName = crule.argL.get(0).strValue();
            DStructHelper helper = new DStructHelper(dval);
            tmp = helper.getField(fieldName);
        }
        boolean pass = tmp.asString().isEmpty();
        return pass;
    }

    @Override
    public void rememberCustomRule(CustomRule exp) {
        this.polarity = exp.polarity;
        crule = exp;
    }
}