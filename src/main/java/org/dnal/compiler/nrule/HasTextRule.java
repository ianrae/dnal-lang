package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


public class HasTextRule extends Custom1RuleBase<VirtualString> { 

    public HasTextRule(String name, VirtualString arg1) {
        super(name, arg1);
    }
    
    @Override
    protected boolean evalNoArg(DValue dval, NRuleContext ctx) {
        if (! dval.getType().isShape(Shape.STRING)) {
            //erro!!
        }

        boolean hasNonWhitespace = false;
        String str = dval.asString();
        for(int i = 0; i < str.length(); i++) {
        	char ch = str.charAt(i);
        	if (! Character.isWhitespace(ch)) {
        		hasNonWhitespace = true;
        		break;
        	}
        }
        
        boolean pass = (!str.isEmpty() && hasNonWhitespace);
        return pass;
    }

}