package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.compiler.parser.ast.NumberExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualNumber;


/**
 * Exclusive range.  The .. syntax not supported for this; must use comma range. eg range(15,20)
 * @author ian
 *
 */
public class NumberRangeRule extends Custom1RuleBase<VirtualNumber>  { 

    public NumberRangeRule(String name, VirtualNumber arg1) {
        super(name, arg1);
    }
    
    @Override
    protected boolean evalDoubleArg(DValue dval, NRuleContext ctx, Exp exp1, Exp exp2) {
        Double from = getDouble(exp1);
        Double to = getDouble(exp2);
        if (to == null || from == null) {
            //!!err
            return false;
        }

        return evaluate(from, to);
    }
    
    private boolean evaluate(Double from, Double to) {
        Double target = arg1.val;
        
        if (target.equals(from)) {
            return true;
        } else {
            return (target >= from && target < to);
        }
    }

    //fix later. for now the arguments are actually int not long
    private Double getDouble(Exp exp) {
        if (exp instanceof LongExp) {
            LongExp intExp = (LongExp) exp;
            return intExp.val.doubleValue();
        } if (exp instanceof IntegerExp) {
            IntegerExp intExp = (IntegerExp) exp;
                return intExp.val.doubleValue();
        } if (exp instanceof NumberExp) {
            NumberExp intExp = (NumberExp) exp;
                return intExp.val.doubleValue();
        } else {
            return null;
        }
    }
}