package org.dnal.compiler.nrule;

import org.dnal.core.DValue;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualInt;

//struct member pseudo-len len(firstName)
public class LenRule extends Custom1Rule<VirtualInt>  { 
	public static final String NAME = "len";
	
    public NRule opRule;
    
    public LenRule(String name, VirtualInt arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        boolean b = opRule.eval(dval, ctx);
        return b;
    }

    @Override
    protected String generateRuleText() {
        return opRule.getRuleText();
    }

}