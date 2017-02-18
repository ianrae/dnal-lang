package org.dnal.compiler.dnalgenerate;

import org.dnal.compiler.parser.ast.ComparisonRuleExp;
import org.dnal.core.DStructType;
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
        } else if (exp.identVal != null) {
            //handle enum. !!later handle reference
            VirtualString vs = new VirtualString();
            return vs;
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

    public static VirtualDataItem createMember(ComparisonRuleExp exp, DType dtype, String fieldName) {
        DStructType structType = (DStructType) dtype;
        DType elType = structType.getFields().get(fieldName);
        
        if (exp.val != null) {
            VirtualIntMember vs = new VirtualIntMember();
            return vs;
        } else if (exp.zval != null) {
            VirtualNumberMember vs = new VirtualNumberMember();
            return vs;
        } else if (exp.strVal != null) {
            if (elType.isShape(Shape.STRING)) {
                VirtualStringMember vs = new VirtualStringMember();
                return vs;
            } else {
                VirtualDateMember vs = new VirtualDateMember();
                return vs;
            }
        } else if (exp.longVal != null) {
            if (elType.isShape(Shape.LONG)) {
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
