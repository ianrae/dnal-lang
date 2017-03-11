package org.dnal.compiler.nrule;

import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualList;


public class EmptyRuleList extends Custom1RuleBase<VirtualList>  { 
    
    public EmptyRuleList(String name, VirtualList arg1) {
        super(name, arg1);
    }
    

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        return arg1.val.isEmpty();
    }
}