package org.dnal.compiler.nrule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


public class RegexRule extends Custom1Rule<VirtualString> implements NeedsCustomRule { 
    public CustomRule crule;

    public RegexRule(String name, VirtualString arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        if (! dval.getType().isShape(Shape.STRING)) {
            //erro!!
        }

        //currently all uses of a rule use same rule object.
        //!!need way to have per-use instances so can compile regex once
        String regexPattern = this.crule.argL.get(0).strValue();
        Pattern compiled = Pattern.compile(regexPattern);
        Log.log(String.format("regex %s:%s", regexPattern, dval.asString()));

        Matcher m = compiled.matcher(dval.asString());
        boolean pass = m.matches();
        return pass;
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