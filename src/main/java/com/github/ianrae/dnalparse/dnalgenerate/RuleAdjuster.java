package com.github.ianrae.dnalparse.dnalgenerate;

import java.util.Date;

import org.dnal.core.DType;
import org.dnal.core.Shape;

import com.github.ianrae.dnalparse.parser.ast.ComparisonAndRuleExp;
import com.github.ianrae.dnalparse.parser.ast.ComparisonOrRuleExp;
import com.github.ianrae.dnalparse.parser.ast.ComparisonRuleExp;
import com.github.ianrae.dnalparse.parser.ast.CustomRule;
import com.github.ianrae.dnalparse.parser.ast.Exp;

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
