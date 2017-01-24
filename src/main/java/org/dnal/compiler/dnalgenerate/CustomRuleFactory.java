package org.dnal.compiler.dnalgenerate;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.Shape;
import org.dnal.core.nrule.NRule;

public class CustomRuleFactory {

    private List<RuleFactory> factoryL = new ArrayList<>();
    
    public void addFactory(RuleFactory factory) {
        factoryL.add(factory);
    }

    public void addRuleDelcarations(List<RuleDeclaration> ruleDeclL) {
        for(RuleFactory factory: factoryL) {
            RuleDeclaration decl = factory.getDeclaration();
            ruleDeclL.add(decl);
        }
    }

//    public NRule findRuleRunner(String fnName) {
//        for(RuleFactory factory: factoryL) {
//            if (factory.willAccept(fnName, null)) {
//                return factory.createRule(fnName, null);
//            }
//        }
//        return null;
//    }
    
    public NRule findRuleRunner(Shape shape, String fnName) {
        for(RuleFactory factory: factoryL) {
            RuleDeclaration decl = factory.getDeclaration();
            if (decl.ruleName.equals(fnName)) {
                for(Shape sh: decl.shapeL) {
                    if (sh.equals(shape)) {
                        return factory.createRule(fnName, shape);
                    }
                }
            }
        }
        return null;
    }
    
}
