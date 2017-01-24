package com.github.ianrae.dnalparse.compiler;

import org.dnal.compiler.dnalgenerate.RuleDeclaration;
import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.compiler.nrule.Custom1Rule;
import org.dnal.compiler.nrule.NeedsCustomRule;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualDate;


public class MyCustomDateRule extends Custom1Rule<VirtualDate> implements NeedsCustomRule { 
    
    public MyCustomDateRule(String name, VirtualDate arg1) {
        super(name, arg1);
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        int yr = arg1.val.getYear();
        return yr == (2016 - 1900);
    }

    @Override
    public void rememberCustomRule(CustomRule exp) {
        this.polarity = exp.polarity;
        //extract args here!!
    }
    
    public static class Factory implements RuleFactory {
        
        @Override
        public NRule createRule(String ruleName, Shape shape) {
            MyCustomDateRule rule = new MyCustomDateRule(ruleName, new VirtualDate());
            rule.setRuleText("year-is-2016");
            return rule;
        }

        @Override
        public RuleDeclaration getDeclaration() {
            RuleDeclaration decl = new RuleDeclaration("mydaterule", Shape.DATE);
            return decl;
        }
    }
}