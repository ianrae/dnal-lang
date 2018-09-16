package org.dnal.compiler.parser.ast;


public class RuleDeclExp extends ExpBase {
    
    public String ruleName;
    public String ruleType;
    
    public RuleDeclExp(int pos, IdentExp ruleNameExp, IdentExp typeExp) {
    	this.pos = pos;
        this.ruleName = ruleNameExp.name();
        this.ruleType = typeExp.name();
    }

    @Override
    public String strValue() {
        return ruleName;
    }

}
