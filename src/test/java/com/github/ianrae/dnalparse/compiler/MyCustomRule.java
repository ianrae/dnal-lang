package com.github.ianrae.dnalparse.compiler;

import org.dval.DValue;
import org.dval.Shape;
import org.dval.nrule.NRule;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.virtual.VirtualString;

import com.github.ianrae.dnalparse.dnalgenerate.RuleDeclaration;
import com.github.ianrae.dnalparse.dnalgenerate.RuleFactory;
import com.github.ianrae.dnalparse.nrule.Custom1Rule;
import com.github.ianrae.dnalparse.nrule.NeedsCustomRule;
import com.github.ianrae.dnalparse.parser.ast.CustomRule;


public class MyCustomRule extends Custom1Rule<VirtualString> implements NeedsCustomRule { 
	
	public MyCustomRule(String name, VirtualString arg1) {
		super(name, arg1);
	}

	@Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
		return arg1.val.contains("a");
	}

    @Override
    public void rememberCustomRule(CustomRule exp) {
        this.polarity = exp.polarity;
    }
    
    public static class Factory implements RuleFactory {

        @Override
        public NRule createRule(String ruleName, Shape shape) {
            MyCustomRule rule = new MyCustomRule(ruleName, new VirtualString());
            rule.setRuleText("contains-a");
            return rule;
        }

        @Override
        public RuleDeclaration getDeclaration() {
            RuleDeclaration decl = new RuleDeclaration("myrule", Shape.STRING);
            return decl;
        }

    }

}