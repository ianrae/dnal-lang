package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.compiler.parser.ast.NumberExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualNumber;


public class InRuleNumber extends Custom1RuleBase<VirtualNumber> { 
    
    public InRuleNumber(String name, VirtualNumber arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        
        boolean found = false;
        for(Exp exp: crule.argL) {
            Double n1 = getDouble(exp);
            if (arg1.val.equals(n1)) {
                found = true;
                break;
            }
        }       
        return found;
    }
    
    private Double getDouble(Exp exp) {
        if (exp instanceof LongExp) {
            LongExp longExp = (LongExp) exp;
            return longExp.val.doubleValue();
        } else if (exp instanceof IntegerExp) {
            IntegerExp intExp = (IntegerExp) exp;
            return intExp.val.doubleValue();
        } else if (exp instanceof NumberExp) {
            NumberExp intExp = (NumberExp) exp;
            return intExp.val.doubleValue();
        } else {
            return null;
        }
    }
}