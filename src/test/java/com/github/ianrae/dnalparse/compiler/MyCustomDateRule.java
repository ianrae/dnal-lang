package com.github.ianrae.dnalparse.compiler;

import org.dval.DValue;
import org.dval.Shape;
import org.dval.nrule.NRule;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.virtual.VirtualDate;

import com.github.ianrae.dnalparse.dnalgenerate.RuleDeclaration;
import com.github.ianrae.dnalparse.dnalgenerate.RuleFactory;
import com.github.ianrae.dnalparse.nrule.Custom1Rule;
import com.github.ianrae.dnalparse.nrule.NeedsCustomRule;
import com.github.ianrae.dnalparse.parser.ast.CustomRule;


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