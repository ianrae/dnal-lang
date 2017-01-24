package com.github.ianrae.dnalparse.nrule;

import org.dnal.core.DValue;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualInt;

//struct member pseudo-len len(firstName)
public class LenRule extends Custom1Rule<VirtualInt>  { 
    public NRule opRule;
    
    public LenRule(String name, VirtualInt arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        boolean b = opRule.eval(dval, ctx);
        return b;
    }

}