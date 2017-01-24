package com.github.ianrae.dnalparse.nrule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dval.DValue;
import org.dval.Shape;
import org.dval.logger.Log;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.virtual.VirtualString;

import com.github.ianrae.dnalparse.parser.ast.CustomRule;


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
}