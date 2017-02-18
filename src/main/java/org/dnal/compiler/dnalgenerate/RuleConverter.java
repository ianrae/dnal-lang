package org.dnal.compiler.dnalgenerate;

import java.util.List;

import org.dnal.api.impl.CompilerContext;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.nrule.IsaRule;
import org.dnal.compiler.nrule.LenRule;
import org.dnal.compiler.nrule.NeedsCustomRule;
import org.dnal.compiler.parser.ast.ComparisonAndRuleExp;
import org.dnal.compiler.parser.ast.ComparisonOrRuleExp;
import org.dnal.compiler.parser.ast.ComparisonRuleExp;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IsaRuleExp;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.Shape;
import org.dnal.core.nrule.AndRule;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.OrRule;

public class RuleConverter extends ErrorTrackingBase {
    private CustomRuleFactory crf;
    private List<RuleDeclaration> ruleDeclL;
    
    public RuleConverter(CustomRuleFactory crf, List<RuleDeclaration> ruleDeclL, XErrorTracker et) {
        super(et);
        this.crf = crf;
        this.ruleDeclL = ruleDeclL;
    }

	public NRule convert(DType type, Exp exp, CompilerContext context) {
		if (exp instanceof ComparisonRuleExp) {
			return doComparisonRule(type, (ComparisonRuleExp) exp);
		} else if (exp instanceof ComparisonOrRuleExp) {
			return doComparisonOrRule(type, (ComparisonOrRuleExp) exp);
		} else if (exp instanceof ComparisonAndRuleExp) {
			return doComparisonAndRule(type, (ComparisonAndRuleExp) exp);
		} else if (exp instanceof IsaRuleExp) {
		    return doIsaRule(type, (IsaRuleExp) exp, context);
		} else if (exp instanceof CustomRule) {
			CustomRule rule = (CustomRule) exp;
			
			NRule z = crf.findRuleRunner(type.getShape(), rule.ruleName);
			if (z == null && type.isShape(Shape.STRUCT)) {
		        DType fieldType = getFieldType(type, rule);
		        if (fieldType ==  null) {
		            return null;
		        }
	            z = crf.findRuleRunner(fieldType.getShape(), rule.ruleName);
			}
			
			if (rule.hackExtra != null) {
			    z = specialHandling(rule, type);
            }
			
			if (z == null) {
                this.addError2s("type %s: unknown rule %s", type.getName(), exp.strValue());
                return null;
			} else if (! checkForRuleDecl(type, rule)) {
			    return null;
			}
			
			//!!fix later. we don't want dtype to have any references to ast exp objects
			if (z instanceof NeedsCustomRule) {
			    NeedsCustomRule ncr = (NeedsCustomRule)z;
			    ncr.rememberCustomRule(rule);
			}
			
			return z;
		}
		return null;
	}
	

	private NRule doIsaRule(DType type, IsaRuleExp exp, CompilerContext context) {
	    return new IsaRule("isa", exp, type, context);
    }

    private boolean checkForRuleDecl(DType type, CustomRule rule) {
        if (type.isScalarShape()) {
            return checkScalarForRuleDecl(type, rule);
        } else if (type.getShape().equals(Shape.STRUCT)) {
            return checkStructForRuleDecl(type, rule);
        } else {
            return false; //handle list later!!
        }
    }
    private boolean checkScalarForRuleDecl(DType type, CustomRule rule) {
        for(RuleDeclaration decl: ruleDeclL) {
            if (rule.ruleName.equals(decl.ruleName)) {
                for(Shape sh: decl.shapeL) {
                    if (sh.equals(type.getShape())) {
                        return true;
                    }
                }
                this.addError3s("type '%s' custom rule '%s' wrong type '%s'", type.getName(), rule.ruleName, type.getShape().name());
                return false;
            }
        }
        this.addError2s("type '%s' rule not declared: '%s'", type.getName(), rule.ruleName);
        return false;
    }
    private boolean checkStructForRuleDecl(DType type, CustomRule rule) {
        DType fieldType = getFieldType(type, rule);
        if (fieldType == null) {
            return false;
        }
        
        return checkForRuleDecl(fieldType, rule); //**recursion**
    }
    private DType getFieldType(DType type, CustomRule rule) {
        String fieldName = getFieldName(rule);

        DStructType structType = (DStructType) type;
        DType fieldType = structType.getFields().get(fieldName);
        if (fieldType == null) {
            this.addError2s("type '%s' rule arg is not a known field: '%s'", type.getName(), fieldName);
            return null;
        }
        
        return fieldType;
    }
    private String getFieldName(CustomRule rule) {
        String fieldName = rule.fieldName;
        
        //legacy syntax len(x)
        if (fieldName == null) {
            fieldName = rule.argL.get(0).strValue();
        }
        return fieldName;
    }

    private NRule doComparisonOrRule(DType type, ComparisonOrRuleExp exp) {
	    NRule  s1 = doScalarComparisonRule(type, exp.exp1);
	    NRule  s2 = doScalarComparisonRule(type, exp.exp2);
		OrRule rule = new OrRule("or", s1, s2);
        rule.setRuleText(String.format("%s %s", "len", s1.getRuleText(), s2.getRuleText()));
		return rule;
	}

	private NRule doComparisonAndRule(DType type, ComparisonAndRuleExp exp) {
	    NRule s1 = doScalarComparisonRule(type, exp.exp1);
	    NRule  s2 = doScalarComparisonRule(type, exp.exp2);
		AndRule rule = new AndRule("and", s1, s2);
        rule.setRuleText(String.format("%s %s", "len", s1.getRuleText(), s2.getRuleText()));
		return rule;
	}
	
    private NRule doComparisonRule(DType type, ComparisonRuleExp exp) {
        if (type.isScalarShape()) {
            boolean isComparable = type.isNumericShape() || type.isShape(Shape.STRING) || type.isShape(Shape.DATE);
            if (! isComparable) {
                this.addError2s("cannot use '%s' on type '%s'. not a comparable type", exp.strValue(), type.getName());
                return null;
            }
            return doScalarComparisonRule(type, exp);
        } else if (type.getShape().equals(Shape.STRUCT)) {
            return doStructComparisonRule(type, exp);
        } else {
            return null; //handle list later!!
        }
    }
	public NRule doScalarComparisonRule(DType type, ComparisonRuleExp exp) {
	    RuleBuilder builder = new RuleBuilder(type);
		switch(exp.op) {
		case "<":
		case ">":
		case "<=":
		case ">=":
		{
		    if (isPseudoLen(exp)) {
		        return builder.buildPseudoLenCompare(exp, false, null);
		    } else {
		        return builder.buildCompare(exp, false);
		    }
		        
		}
		case "==":
		case "!=":
		{
            if (isPseudoLen(exp)) {
                return builder.buildPseudoLenEq(exp, false, null);
            } else {
                return builder.buildEq(exp, false);
            }
		}
		default:
			return null;
		}
	}
	
	private boolean isPseudoLen(ComparisonRuleExp exp) {
	    if (exp.optionalArg != null) {
	        return exp.optionalArg.name().equals("len");
	    }
        return false;
    }

    private NRule doStructComparisonRule(DType type, ComparisonRuleExp exp) {
        RuleBuilder builder = new RuleBuilder(type);

        switch(exp.op) {
        case "<":
        case ">":
        case "<=":
        case ">=":
        {
            return builder.buildCompare(exp, true);
        }
        case "==":
        case "!=":
        {
            return builder.buildEq(exp, true);
        }
        default:
            return null;
        }
    }
    
    //len
    private NRule specialHandling(CustomRule rule, DType type) {
        RuleBuilder builder = new RuleBuilder(type);
        ComparisonRuleExp exp = (ComparisonRuleExp) rule.hackExtra;
        String fieldName = getFieldName(rule);
        
        
        LenRule newRule = null;
        switch(exp.op) {
        case "<":
        case ">":
        case "<=":
        case ">=":
            newRule = builder.buildPseudoLenCompare(exp, true, fieldName);
            break;
        case "==":
        case "!=":
            newRule = builder.buildPseudoLenEq(exp, true, fieldName);
            break;
        default:
            newRule = null;
            break;
        }
        return newRule;
        
    }

    
}
