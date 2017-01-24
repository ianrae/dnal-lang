package com.github.ianrae.dnalparse.nrule;

import org.dval.DValue;
import org.dval.Shape;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.virtual.VirtualString;

import com.github.ianrae.dnalparse.parser.ast.CustomRule;


public class EndsWithRule extends Custom1Rule<VirtualString> implements NeedsCustomRule { 
    public CustomRule crule;

    public EndsWithRule(String name, VirtualString arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        if (! dval.getType().isShape(Shape.STRING)) {
            //erro!!
        }

        String str = this.crule.argL.get(0).strValue();
        boolean pass = dval.asString().endsWith(str);
        return pass;
    }

    @Override
    public void rememberCustomRule(CustomRule exp) {
        this.polarity = exp.polarity;
        crule = exp;
    }
}