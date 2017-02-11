package org.dnal.compiler.nrule;

import java.util.Date;

import org.dnal.compiler.dnalgenerate.DateFormatParser;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.compiler.parser.ast.RangeExp;
import org.dnal.compiler.parser.ast.StringExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualDate;


/**
 * Exclusive range.  Which means range(0..100) means 0,1,..99.
 * @author ian
 *
 */
public class DateRangeRule extends Custom1RuleBase<VirtualDate> { 

    public DateRangeRule(String name, VirtualDate arg1) {
        super(name, arg1);
    }
    
    @Override
    protected boolean evalDoubleArg(DValue dval, NRuleContext ctx, Exp exp1, Exp exp2) {
        Date from = getDate(exp1);
        Date to = getDate(exp2);
        if (to == null || from == null) {
            //!!err
            return false;
        }
        return evaluate(from, to);
    }
    
    private boolean evaluate(Date from, Date to) {
        Date target = arg1.val;
        
        if (target.equals(from)) {
            return true;
        } else {
            boolean b1 = target.after(from);
            boolean b2 = target.before(to);
            return (b1 && b2);
        }
    }

    @Override
    protected boolean evalSingleArg(DValue dval, NRuleContext ctx, Exp exp) {
        addWrongArgumentsError(ctx);
        return false;
    }

    private Date getDate(Exp exp) {
        if (exp instanceof LongExp) {
            LongExp longExp = (LongExp) exp;
            Date dt = new Date(longExp.val);
            return dt;
        } else if (exp instanceof StringExp) {
            StringExp strExp = (StringExp) exp;
            Date dt = DateFormatParser.parse(strExp.val);
            return dt;
        } else {
            return null;
        }
    }
}