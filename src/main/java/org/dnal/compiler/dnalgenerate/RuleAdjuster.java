package org.dnal.compiler.dnalgenerate;

import java.util.Date;

import org.dnal.compiler.parser.ast.ComparisonAndRuleExp;
import org.dnal.compiler.parser.ast.ComparisonOrRuleExp;
import org.dnal.compiler.parser.ast.ComparisonRuleExp;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DType;
import org.dnal.core.Shape;

public class RuleAdjuster {

    public void adjust(DType type, Exp exp) {
        if (type.isShape(Shape.DATE)) {
            handleDate(type, exp);
        }
    }

    private void handleDate(DType type, Exp exp) {
        if (exp instanceof ComparisonRuleExp) {
            doComparisonRule(type, (ComparisonRuleExp) exp);
        } else if (exp instanceof ComparisonOrRuleExp) {
            doComparisonOrRule(type, (ComparisonOrRuleExp) exp);
        } else if (exp instanceof ComparisonAndRuleExp) {
            doComparisonAndRule(type, (ComparisonAndRuleExp) exp);
        } else if (exp instanceof CustomRule) {
            //later!!
        }
    }

    private void doComparisonOrRule(DType type, ComparisonOrRuleExp exp) {
        doComparisonRule(type, exp.exp1);
        doComparisonRule(type, exp.exp2);
    }

    private void doComparisonAndRule(DType type, ComparisonAndRuleExp exp) {
        doComparisonRule(type, exp.exp1);
        doComparisonRule(type, exp.exp2);
    }
    
    private void doComparisonRule(DType type, ComparisonRuleExp exp) {
        if (exp.strVal != null) {
            Date dt = DateFormatParser.parse(exp.strVal);
            if (dt != null) {
                exp.longVal = dt.getTime();
                exp.strVal = null;
            }
        }
    }

}
