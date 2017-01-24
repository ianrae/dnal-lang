package com.github.ianrae.dnalparse.dnalgenerate;

import org.dnal.core.Shape;
import org.dnal.core.nrule.NRule;

public interface RuleFactory {

    RuleDeclaration getDeclaration();
//    boolean willAccept(String ruleName, Shape shape);
    NRule createRule(String ruleName, Shape shape);
    
}
