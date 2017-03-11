package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


public class EmptyRule extends Custom1RuleBase<VirtualString>  { 
    
    public EmptyRule(String name, VirtualString arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean evalNoArg(DValue dval, NRuleContext ctx) {
        DValue tmp = dval;
        boolean pass = tmp.asString().isEmpty();
        return pass;
    }

    @Override
    protected boolean evalSingleArg(DValue dval, NRuleContext ctx, Exp exp) {
        DValue tmp = dval;
        
        if (dval.getType().isShape(Shape.STRUCT)) {
            String fieldName = crule.argL.get(0).strValue();
            DStructHelper helper = new DStructHelper(dval);
            tmp = helper.getField(fieldName);
        }
        boolean pass = tmp.asString().isEmpty();
        return pass;
    }
}