package com.github.ianrae.dnalparse.parser.ast;


public class RuleDeclExp implements Exp {
    
    public String ruleName;
    public String ruleType;
    
    public RuleDeclExp(IdentExp ruleNameExp, IdentExp typeExp) {
        this.ruleName = ruleNameExp.name();
        this.ruleType = typeExp.name();
    }

    @Override
    public String strValue() {
        return ruleName;
    }

}
