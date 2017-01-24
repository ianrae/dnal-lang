package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


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