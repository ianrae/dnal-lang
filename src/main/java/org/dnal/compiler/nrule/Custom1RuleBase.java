package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.ErrorType;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualDataItem;


/**
 * Exclusive range.  Which means range(0..100) means 0,1,..99.
 * @author ian
 *
 */
public abstract class Custom1RuleBase<T extends VirtualDataItem> extends Custom1Rule<T> implements NeedsCustomRule { 
    public CustomRule crule;

    public Custom1RuleBase(String name, T arg1) {
        super(name, arg1);
    }

    protected void addWrongArgumentsError(NRuleContext ctx) {
        ErrorMessage err = new ErrorMessage(ErrorType.INVALIDRULE, 
                String.format("wrong number of arguments: %s", crule.strValue()));
        ctx.addError(err);
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
    
    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        if (crule.argL.size() == 0) {
            return evalNoArg(dval, ctx);
        } else if (crule.argL.size() == 1) {
            return evalSingleArg(dval, ctx, crule.argL.get(0));
        } else if (crule.argL.size() == 2) {
            return evalDoubleArg(dval, ctx, crule.argL.get(0), crule.argL.get(1));
        } else {
            addWrongArgumentsError(ctx);
            return false;
        }
    }

    protected boolean evalNoArg(DValue dval, NRuleContext ctx) {
        addWrongArgumentsError(ctx);
        return false;
    }
    protected boolean evalSingleArg(DValue dval, NRuleContext ctx, Exp exp) {
        addWrongArgumentsError(ctx);
        return false;
    }
    protected boolean evalDoubleArg(DValue dval, NRuleContext ctx, Exp exp1, Exp exp2) {
        addWrongArgumentsError(ctx);
        return false;
    }

}