package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.StringExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


/**
 * Exclusive range.  The .. syntax not supported for this; must use comma range. eg range('aaa','zzz')
 * @author ian
 *
 */
public class StringRangeRule extends Custom1RuleBase<VirtualString>  { 
    private boolean isCaseSensitive;

    public StringRangeRule(String name, VirtualString arg1, boolean isCaseSensitive) {
        super(name, arg1);
        this.isCaseSensitive = isCaseSensitive;
    }
    
    @Override
    protected boolean evalDoubleArg(DValue dval, NRuleContext ctx, Exp exp1, Exp exp2) {
        String from = getString(exp1);
        String to = getString(exp2);
        if (to == null || from == null) {
            //!!err
            return false;
        }

        return (isCaseSensitive) ? evaluate(from, to) : evaluatei(from, to);
    }
    
    private boolean evaluate(String from, String to) {
        String target = arg1.val;
        
        if (target.equals(from)) {
            return true;
        } else {
            int cmp1 = target.compareTo(from);
            int cmp2 = target.compareTo(to);
            return (cmp1 >= 0 && cmp2 < 0);
        }
    }
    private boolean evaluatei(String from, String to) {
        String target = arg1.val;
        
        if (target.equals(from)) {
            return true;
        } else {
            int cmp1 = target.compareToIgnoreCase(from);
            int cmp2 = target.compareToIgnoreCase(to);
            return (cmp1 >= 0 && cmp2 < 0);
        }
    }

    private String getString(Exp exp) {
        if (exp instanceof StringExp) {
            StringExp strExp = (StringExp) exp;
            return strExp.val;
        } else {
            return null;
        }
    }
}