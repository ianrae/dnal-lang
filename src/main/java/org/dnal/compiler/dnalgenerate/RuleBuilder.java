package org.dnal.compiler.dnalgenerate;

import java.util.Date;

import org.dnal.compiler.nrule.LenRule;
import org.dnal.compiler.parser.ast.ComparisonRuleExp;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.Shape;
import org.dnal.core.nrule.CompareRule;
import org.dnal.core.nrule.EqRule;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.virtual.StructMember;
import org.dnal.core.nrule.virtual.VirtualDataItem;
import org.dnal.core.nrule.virtual.VirtualDate;
import org.dnal.core.nrule.virtual.VirtualInt;
import org.dnal.core.nrule.virtual.VirtualLong;
import org.dnal.core.nrule.virtual.VirtualNumber;
import org.dnal.core.nrule.virtual.VirtualPseudoLen;
import org.dnal.core.nrule.virtual.VirtualString;

public class RuleBuilder {
    private DType dtype;
    
    public RuleBuilder(DType type) {
        this.dtype = type;
    }
    
    public boolean isCompatibleType(ComparisonRuleExp exp) {
        
        if (exp.val != null) {
            return dtype.isShape(Shape.INTEGER) || dtype.isShape(Shape.LONG);
        } else if (exp.zval != null) {
            return dtype.isShape(Shape.NUMBER);
        } else if (exp.strVal != null) {
            return dtype.isShape(Shape.STRING); // || dtype.isShape(Shape.ENUM);
        } else if (exp.longVal != null) {
            return dtype.isShape(Shape.LONG) || dtype.isShape(Shape.DATE);
        } else if (exp.identVal != null) {
            return dtype.isShape(Shape.ENUM);
        } else {
            return false;
        }
    }
    public boolean isCompatibleMemberType(ComparisonRuleExp exp) {
        VirtualDataItem vs = createVirtual(exp, true);
        StructMember sm = (StructMember) vs;
        
        DStructType structType = (DStructType) dtype;
        String fieldName = sm.getFieldName();
        DType elType =  structType.getFields().get(fieldName);

        if (exp.val != null) {
            return elType.isShape(Shape.INTEGER) || elType.isShape(Shape.LONG);
        } else if (exp.zval != null) {
            return elType.isShape(Shape.NUMBER);
        } else if (exp.strVal != null) {
            return elType.isShape(Shape.STRING) || elType.isShape(Shape.DATE);
        } else if (exp.longVal != null) {
            return elType.isShape(Shape.LONG) || elType.isShape(Shape.DATE);
        } else if (exp.identVal != null) {
            return elType.isShape(Shape.ENUM);
        } else {
            return false;
        }
    }

    public NRule buildCompare(String ruleName, ComparisonRuleExp exp, boolean isMember) {
        VirtualDataItem vs = createVirtual(exp, isMember);
        ruleName = CompareRule.NAME + "-" + ruleName;
        
        if (exp.val != null) {
            return doBuildIntCompare(ruleName, exp, (VirtualInt)vs);
        } else if (exp.zval != null) {
            return doBuildNumberCompare(ruleName, exp, (VirtualNumber)vs);
        } else if (exp.strVal != null) {
            if (vs.getTargetShape().equals(Shape.STRING)) {
                return doBuildStringCompare(ruleName, exp, (VirtualString)vs);
            } else {
                return doBuildDateCompare(ruleName, exp, (VirtualDate)vs);
            }
        } else if (exp.longVal != null) {
            if (vs.getTargetShape().equals(Shape.LONG)) {
                return doBuildLongCompare(ruleName, exp, (VirtualLong) vs);
            } else {
                return doBuildDateCompare(ruleName, exp, (VirtualDate)vs);
            }
        } else {
            return null; //!!
        }
    }
    
    private VirtualDataItem createVirtual(ComparisonRuleExp exp, boolean isMember) {
        VirtualDataItem vs;
        if (isMember) {
            String fieldName = getFieldName(exp);
            vs = VirtualFactory.createMember(exp, dtype, fieldName);
            StructMember sm = (StructMember) vs;
            sm.setFieldName(fieldName);
        } else {
            vs = VirtualFactory.create(exp, dtype);
        }
        return vs;
    }
    private VirtualPseudoLen createVirtualPseudoLen(ComparisonRuleExp exp, boolean isMember, String fieldName) {
        VirtualPseudoLen vs = VirtualFactory.createPseudoLen(exp, isMember);
        if (isMember) {
            StructMember sm = (StructMember) vs;
            sm.setFieldName(fieldName);
        }
        return vs;
    }
    
//    public NRule buildIntCompare(ComparisonRuleExp exp) {
//        VirtualInt vs = new VirtualInt();
//        return doBuildIntCompare(exp, vs);
//    }
    private NRule doBuildIntCompare(String ruleName, ComparisonRuleExp exp, VirtualInt vs) {
        NRule rule = new CompareRule<VirtualInt, Integer>(ruleName, exp.op, vs, exp.val);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildNumberCompare(String ruleName, ComparisonRuleExp exp, VirtualNumber vs) {
        NRule rule = new CompareRule<VirtualNumber, Double>(ruleName, exp.op, vs, exp.zval);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildStringCompare(String ruleName, ComparisonRuleExp exp, VirtualString vs) {
        NRule rule = new CompareRule<VirtualString, String>(ruleName, exp.op, vs, exp.strVal);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildLongCompare(String ruleName, ComparisonRuleExp exp, VirtualLong vs) {
        NRule rule = new CompareRule<VirtualLong, Long>(ruleName, exp.op, vs, exp.longVal);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildDateCompare(String ruleName, ComparisonRuleExp exp, VirtualDate vs) {
        Date dt = null;
        if (exp.longVal != null) {
            dt = new Date(exp.longVal);
        } else if (exp.strVal != null) {
            dt = DateFormatParser.parse(exp.strVal);
        }
        
        NRule rule = new CompareRule<VirtualDate, Date>(ruleName, exp.op, vs, dt);
        rule.setRuleText(exp.strValue());
        return rule;
    }

    
    public NRule buildEq(String ruleName, ComparisonRuleExp exp, boolean isMember) {
        VirtualDataItem vs = createVirtual(exp, isMember);
        ruleName = EqRule.NAME + "-" + ruleName;
        if (exp.val != null) {
            return doBuildIntEq(ruleName, exp, (VirtualInt)vs);
        } else if (exp.zval != null) {
            return doBuildNumberEq(ruleName, exp, (VirtualNumber)vs);
        } else if (exp.strVal != null) {
            if (vs.getTargetShape().equals(Shape.STRING)) {
                return doBuildStringEq(ruleName, exp, (VirtualString)vs);
            } else {
                return doBuildDateEq(ruleName, exp, (VirtualDate)vs);
            }
        } else if (exp.longVal != null) {
            if (vs.getTargetShape().equals(Shape.LONG)) {
                return doBuildLongEq(ruleName, exp, (VirtualLong)vs);
            } else {
                return doBuildDateEq(ruleName, exp, (VirtualDate)vs);
            }
        } else if (exp.identVal != null) {
            return doBuildEnumStringEq(ruleName, exp, (VirtualString)vs);
        } else {
            return null; //!!
        }
    }
//    public NRule buildIntEq(ComparisonRuleExp exp) {
//        VirtualInt vs = new VirtualInt();
//        return doBuildIntEq(exp, vs);
//    }
    private NRule doBuildIntEq(String ruleName, ComparisonRuleExp exp, VirtualInt vs) {
        NRule rule = new EqRule<VirtualInt, Integer>(ruleName, exp.op, vs, exp.val);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildNumberEq(String ruleName, ComparisonRuleExp exp, VirtualNumber vs) {
        NRule rule = new EqRule<VirtualNumber, Double>(ruleName, exp.op, vs, exp.zval);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildStringEq(String ruleName, ComparisonRuleExp exp, VirtualString vs) {
        NRule rule = new EqRule<VirtualString, String>(ruleName, exp.op, vs, exp.strVal);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildEnumStringEq(String ruleName, ComparisonRuleExp exp, VirtualString vs) {
        NRule rule = new EqRule<VirtualString, String>(ruleName, exp.op, vs, exp.identVal);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildLongEq(String ruleName, ComparisonRuleExp exp, VirtualLong vs) {
        NRule rule = new EqRule<VirtualLong, Long>(ruleName, exp.op, vs, exp.longVal);
        rule.setRuleText(exp.strValue());
        return rule;
    }
    private NRule doBuildDateEq(String ruleName, ComparisonRuleExp exp, VirtualDate vs) {
        Date dt = new Date(exp.longVal);
        NRule rule = new EqRule<VirtualDate, Date>(ruleName, exp.op, vs, dt);
        rule.setRuleText(exp.strValue());
        return rule;
    }

    public LenRule buildPseudoLenCompare(String ruleName, ComparisonRuleExp exp, boolean isMember, String fieldName) {
        VirtualPseudoLen vs = this.createVirtualPseudoLen(exp, isMember, fieldName);
        NRule inner = doBuildIntCompare(ruleName, exp, vs);
        LenRule newRule = new LenRule(LenRule.NAME + "-" + ruleName, vs);
        newRule.opRule = inner;
        newRule.setRuleText(String.format("%s %s", LenRule.NAME, inner.getRuleText()));
        return newRule;
    }
    public LenRule buildPseudoLenEq(String ruleName, ComparisonRuleExp exp, boolean isMember, String fieldName) {
        VirtualDataItem vs = this.createVirtualPseudoLen(exp, isMember, fieldName);
        NRule inner = doBuildIntEq(ruleName, exp, (VirtualInt)vs);
        LenRule newRule = new LenRule(LenRule.NAME + "-" + ruleName, (VirtualInt) vs);
        newRule.setRuleText(String.format("%s %s", LenRule.NAME, inner.getRuleText()));
        newRule.opRule = inner;
        return newRule;
    }
    
    public String getFieldName(ComparisonRuleExp exp) {
    	if (exp.optionalArg instanceof IdentExp) {
    		IdentExp iexp = (IdentExp) exp.optionalArg;
    		return iexp.name();
    	} else if (exp.optionalArg instanceof CustomRule) {
    		CustomRule iexp = (CustomRule) exp.optionalArg;
    		return iexp.fieldName;
    	} else {
    		return null;
    	}
//        String fieldName = (exp.optionalArg == null) ? null : exp.optionalArg.name();
//        return fieldName;
    }
}
