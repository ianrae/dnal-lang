package com.github.ianrae.dnalparse.dnalgenerate;

import org.dnal.core.DType;
import org.dnal.core.Shape;
import org.dnal.core.nrule.virtual.VirtualDataItem;
import org.dnal.core.nrule.virtual.VirtualDate;
import org.dnal.core.nrule.virtual.VirtualDateMember;
import org.dnal.core.nrule.virtual.VirtualInt;
import org.dnal.core.nrule.virtual.VirtualIntMember;
import org.dnal.core.nrule.virtual.VirtualLong;
import org.dnal.core.nrule.virtual.VirtualLongMember;
import org.dnal.core.nrule.virtual.VirtualNumber;
import org.dnal.core.nrule.virtual.VirtualNumberMember;
import org.dnal.core.nrule.virtual.VirtualPseudoLen;
import org.dnal.core.nrule.virtual.VirtualPseudoLenMember;
import org.dnal.core.nrule.virtual.VirtualString;
import org.dnal.core.nrule.virtual.VirtualStringMember;

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
