package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualList;


public class ContainsRuleList extends Custom1RuleBase<VirtualList> { 

    public ContainsRuleList(String name, VirtualList arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        int failCount = 0;
        for(Exp exp: crule.argL) {
            String str = exp.strValue();
            for(DValue el: arg1.val) {
                if (! el.asString().equals(str))  {
                    failCount++;
                }
            }
        }

        return failCount == 0;
    }

    private boolean compare(Exp exp, DValue el) {
        if (el.getType().isShape(Shape.STRING)) {
            String str = exp.strValue();
            if (! el.asString().equals(str))  {
                return false;
            }
        } else if (el.getType().isShape(Shape.INTEGER)) {
            Integer nval = Integer.parseInt(exp.strValue()); //!!make more efficient later!!
            if (el.asInt() != nval.intValue())  {
                return false;
            }
        } else if (el.getType().isShape(Shape.LONG)) {
            Long nval = Long.parseLong(exp.strValue()); //!!make more efficient later!!
            if (el.asLong() != nval.longValue())  {
                return false;
            }
        } //!!support more types later
        return true;
    }

}