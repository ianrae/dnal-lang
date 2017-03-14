package org.dnal.compiler.core;

import org.dnal.compiler.dnalgenerate.RuleDeclaration;
import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.compiler.nrule.Custom1Rule;
import org.dnal.compiler.nrule.NeedsCustomRule;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;


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

    @Override
    protected String generateRuleText() {
        return getName();
    }

}