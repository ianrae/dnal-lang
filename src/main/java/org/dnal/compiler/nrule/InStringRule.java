package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.StringExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


public class InStringRule extends Custom1RuleBase<VirtualString> { 
    
    public InStringRule(String name, VirtualString arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        
        boolean found = false;
        for(Exp exp: crule.argL) {
            StringExp strExp = (StringExp) exp;
            if (arg1.val.equals(strExp.val)) {
                found = true;
                break;
            }
        }       
        return found;
    }
    
}