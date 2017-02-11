package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


public class StartsWithRule extends Custom1RuleBase<VirtualString> { 

    public StartsWithRule(String name, VirtualString arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean evalSingleArg(DValue dval, NRuleContext ctx, Exp exp) {
        if (! dval.getType().isShape(Shape.STRING)) {
            //erro!!
        }

        String str = exp.strValue();
        boolean pass = dval.asString().startsWith(str);
        return pass;
    }

    @Override
    public void rememberCustomRule(CustomRule exp) {
        this.polarity = exp.polarity;
        crule = exp;
    }

    @Override
    protected String generateRuleText() {
        return crule.strValue();
    }
}