package com.github.ianrae.dnalparse.nrule;

import org.dval.DValue;
import org.dval.nrule.NRule;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.virtual.VirtualInt;

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