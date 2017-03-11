package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.StringExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


/**
 * @author ian
 *
 */
public class StringCompareCaseInsensitiveRule extends Custom1RuleBase<VirtualString>  { 

    private String op; //==,>,<,<=,>=

    public StringCompareCaseInsensitiveRule(String name, VirtualString arg1, String op) {
        super(name, arg1);
        this.op = op;
    }
    
    @Override
    protected boolean evalSingleArg(DValue dval, NRuleContext ctx, Exp exp1) {
        String from = getString(exp1);
        if (from == null) {
            //!!err
            return false;
        }
        
        String target = arg1.val;
        boolean pass = false;
        switch(op) {
        case "==":
            pass = target.equalsIgnoreCase(from);
            break;
        case ">":
            pass = target.compareToIgnoreCase(from) > 0;
            break;
        case ">=":
            pass = target.compareToIgnoreCase(from) >= 0;
            break;
        case "<":
            pass = target.compareToIgnoreCase(from) < 0;
            break;
        case "<=":
            pass = target.compareToIgnoreCase(from) <= 0;
            break;
        default:
            break;
        }
        
        return pass;
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