package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.compiler.parser.ast.RangeExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualInt;


/**
 * Exclusive range.  Which means range(0..100) means 0,1,..99.
 * @author ian
 *
 */
public class IntegerRangeRule extends Custom1RuleBase<VirtualInt>  { 

    public IntegerRangeRule(String name, VirtualInt arg1) {
        super(name, arg1);
    }
    
    @Override
    protected boolean evalDoubleArg(DValue dval, NRuleContext ctx, Exp exp1, Exp exp2) {
        Integer from = getInt(exp1);
        Integer to = getInt(exp2);
        if (to == null || from == null) {
            //!!err
            return false;
        }

        return evaluate(from, to);
    }
    
    private boolean evaluate(Integer from, Integer to) {
        Integer target = arg1.val;
        
        if (target.equals(from)) {
            return true;
        } else {
            return (target >= from && target < to);
        }
    }

    @Override
    protected boolean evalSingleArg(DValue dval, NRuleContext ctx, Exp exp) {
        RangeExp rexp = (RangeExp) exp;
        return evaluate(rexp.from, rexp.to);
    }


    private Integer getInt(Exp exp) {
        if (exp instanceof LongExp) {
            LongExp longExp = (LongExp) exp;
            return longExp.val.intValue();
        } else if (exp instanceof IntegerExp) {
            IntegerExp intExp = (IntegerExp) exp;
            return intExp.val;
        } else {
            return null;
        }
    }
}