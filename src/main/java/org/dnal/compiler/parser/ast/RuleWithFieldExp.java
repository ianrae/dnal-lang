package org.dnal.compiler.parser.ast;


public class RuleWithFieldExp extends ExpBase {
    
    public String fieldName;
    public String ruleName;
    
    public RuleWithFieldExp(IdentExp nameExp, IdentExp fieldExp) {
        this.ruleName = nameExp.name();
        this.fieldName = (fieldExp == null) ? null : fieldExp.name();
    }

    @Override
    public String strValue() {
        return (fieldName == null) ? ruleName : String.format("%s.%s", fieldName, ruleName);
    }

}
