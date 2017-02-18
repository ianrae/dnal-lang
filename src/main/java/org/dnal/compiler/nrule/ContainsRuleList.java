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
    protected boolean evalSingleArg(DValue dval, NRuleContext ctx, Exp exp) {
        if (! dval.getType().isShape(Shape.STRING)) {
            //erro!!
        }

        String str = exp.strValue();
        boolean pass = dval.asString().contains(str);
        return pass;
    }

}