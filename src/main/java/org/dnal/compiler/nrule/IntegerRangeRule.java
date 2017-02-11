package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualInt;


/**
 * Exclusive range.  Which means range(0..100) means 0,1,..99.
 * @author ian
 *
 */
public class IntegerRangeRule extends Custom1Rule<VirtualInt> implements NeedsCustomRule { 
    public CustomRule crule;

    public IntegerRangeRule(String name, VirtualInt arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        if (crule.argL.size() != 2) {
            this.addInvalidRuleError(ctx, this.getRuleText());
//          this.addInvalidRuleError(ruleText);
            return false;
        }

        Integer from = getInt(crule.argL.get(0));
        Integer to = getInt(crule.argL.get(1));
        if (to == null || from == null) {
            //!!err
            return false;
        }

        Integer target = arg1.val;
        
        if (target.equals(from)) {
            return true;
        } else {
            return (target >= from && target < to);
        }
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

    @Override
    public void rememberCustomRule(CustomRule exp) {
        this.polarity = exp.polarity;
        crule = exp;
    }
}