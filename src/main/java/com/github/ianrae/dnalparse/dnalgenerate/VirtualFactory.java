package com.github.ianrae.dnalparse.dnalgenerate;

import org.dval.DType;
import org.dval.Shape;
import org.dval.nrule.virtual.VirtualDataItem;
import org.dval.nrule.virtual.VirtualDate;
import org.dval.nrule.virtual.VirtualDateMember;
import org.dval.nrule.virtual.VirtualInt;
import org.dval.nrule.virtual.VirtualIntMember;
import org.dval.nrule.virtual.VirtualLong;
import org.dval.nrule.virtual.VirtualLongMember;
import org.dval.nrule.virtual.VirtualNumber;
import org.dval.nrule.virtual.VirtualNumberMember;
import org.dval.nrule.virtual.VirtualPseudoLen;
import org.dval.nrule.virtual.VirtualPseudoLenMember;
import org.dval.nrule.virtual.VirtualString;
import org.dval.nrule.virtual.VirtualStringMember;

import com.github.ianrae.dnalparse.parser.ast.ComparisonRuleExp;

public class VirtualFactory {
    public static VirtualDataItem create(ComparisonRuleExp exp, DType dtype) {
        if (exp.val != null) {
            VirtualInt vs = new VirtualInt();
            return vs;
        } else if (exp.zval != null) {
            VirtualNumber vs = new VirtualNumber();
            return vs;
        } else if (exp.strVal != null) {
            VirtualString vs = new VirtualString();
            return vs;
        } else if (exp.longVal != null) {
            if (dtype.isShape(Shape.LONG)) {
                return new VirtualLong();
            } else {
                VirtualDate vs = new VirtualDate();
                return vs;
            }
        } else {
            return null; //!!
        }
    }
    
    public static VirtualPseudoLen createPseudoLen(ComparisonRuleExp exp, boolean isMember) {
        VirtualPseudoLen vs;
        if (isMember) {
            vs = new VirtualPseudoLenMember();
        } else {
            vs = new VirtualPseudoLen();
        }
        return vs;
    }

    public static VirtualDataItem createMember(ComparisonRuleExp exp, DType dtype) {
        if (exp.val != null) {
            VirtualIntMember vs = new VirtualIntMember();
            return vs;
        } else if (exp.zval != null) {
            VirtualNumberMember vs = new VirtualNumberMember();
            return vs;
        } else if (exp.strVal != null) {
            VirtualStringMember vs = new VirtualStringMember();
            return vs;
        } else if (exp.longVal != null) {
            if (dtype.isShape(Shape.LONG)) {
                return new VirtualLongMember();
            } else {
                VirtualDateMember vs = new VirtualDateMember();
                return vs;
            }
        } else {
            return null; //!!
        }
    }
    
}
