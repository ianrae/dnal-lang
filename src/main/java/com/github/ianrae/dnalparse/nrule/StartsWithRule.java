package com.github.ianrae.dnalparse.nrule;

import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;

import com.github.ianrae.dnalparse.parser.ast.CustomRule;


public class StartsWithRule extends Custom1Rule<VirtualString> implements NeedsCustomRule { 
    public CustomRule crule;

    public StartsWithRule(String name, VirtualString arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        if (! dval.getType().isShape(Shape.STRING)) {
            //erro!!
        }

        String str = this.crule.argL.get(0).strValue();
        boolean pass = dval.asString().startsWith(str);
        return pass;
    }

    @Override
    public void rememberCustomRule(CustomRule exp) {
        this.polarity = exp.polarity;
        crule = exp;
    }
}