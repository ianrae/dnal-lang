package org.dnal.compiler.parser.ast;


public class RuleDeclExp extends ExpBase {
    
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
