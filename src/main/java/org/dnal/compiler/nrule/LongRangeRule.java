package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualLong;


/**
 * Exclusive range.  The .. syntax not supported for this; must use comma range. eg range(15,20)
 * @author ian
 *
 */
public class LongRangeRule extends Custom1RuleBase<VirtualLong>  { 

    public LongRangeRule(String name, VirtualLong arg1) {
        super(name, arg1);
    }
    
    @Override
    protected boolean evalDoubleArg(DValue dval, NRuleContext ctx, Exp exp1, Exp exp2) {
        Long from = getLong(exp1);
        Long to = getLong(exp2);
        if (to == null || from == null) {
            //!!err
            return false;
        }

        return evaluate(from, to);
    }
    
    private boolean evaluate(Long from, Long to) {
        Long target = arg1.val;
        
        if (target.equals(from)) {
            return true;
        } else {
            return (target >= from && target < to);
        }
    }

    //fix later. for now the arguments are actually int not long
    private Long getLong(Exp exp) {
        if (exp instanceof LongExp) {
            LongExp intExp = (LongExp) exp;
            return intExp.val;
        } if (exp instanceof IntegerExp) {
            IntegerExp intExp = (IntegerExp) exp;
                return intExp.val.longValue();
        } else {
            return null;
        }
    }
}