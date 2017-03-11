package org.dnal.compiler.nrule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


public class RegexRule extends Custom1RuleBase<VirtualString> { 

    public RegexRule(String name, VirtualString arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean evalSingleArg(DValue dval, NRuleContext ctx, Exp exp) {
        if (! dval.getType().isShape(Shape.STRING)) {
            //erro!!
        }

        //currently all uses of a rule use same rule object.
        //!!need way to have per-use instances so can compile regex once
        String regexPattern = exp.strValue();
        Pattern compiled = Pattern.compile(regexPattern);
        Log.log(String.format("regex %s:%s", regexPattern, dval.asString()));

        Matcher m = compiled.matcher(dval.asString());
        boolean pass = m.matches();
        return pass;
    }
}