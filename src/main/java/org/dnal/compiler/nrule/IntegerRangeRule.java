package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.compiler.parser.ast.RangeExp;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.ErrorType;
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
    
    
    protected void addWrongArgumentsError(NRuleContext ctx, String ruleText, CustomRule crulex) {
        ErrorMessage err = new ErrorMessage(ErrorType.INVALIDRULE, 
                String.format("wrong number of arguments: %s", crulex.strValue()));
        ctx.addError(err);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        if (crule.argL.size() == 1) {
            return evalRangeExp(dval, ctx);
        } else if (crule.argL.size() == 2) {
            return evalCommaRange(dval, ctx);
        } else {
            addWrongArgumentsError(ctx, "", crule);
//          this.addInvalidRuleError(ruleText);
            return false;
        }

    }

    private boolean evalCommaRange(DValue dval, NRuleContext ctx) {
        Integer from = getInt(crule.argL.get(0));
        Integer to = getInt(crule.argL.get(1));
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


    private boolean evalRangeExp(DValue dval, NRuleContext ctx) {
        RangeExp rexp = (RangeExp) crule.argL.get(0);
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

    @Override
    public void rememberCustomRule(CustomRule exp) {
        this.polarity = exp.polarity;
        crule = exp;
    }

    @Override
    protected String generateRuleText() {
        return crule.strValue();
    }
}