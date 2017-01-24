package com.github.ianrae.dnalparse.dnalgenerate;

import org.dval.Shape;
import org.dval.nrule.NRule;

public interface RuleFactory {

    RuleDeclaration getDeclaration();
//    boolean willAccept(String ruleName, Shape shape);
    NRule createRule(String ruleName, Shape shape);
    
}
