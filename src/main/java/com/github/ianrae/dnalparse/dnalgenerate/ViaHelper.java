package com.github.ianrae.dnalparse.dnalgenerate;

import org.dval.DListType;
import org.dval.DStructType;
import org.dval.DType;
import org.dval.Shape;
import org.dval.nrule.NRule;

import com.github.ianrae.dnalparse.nrule.IsaRule;
import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.IdentExp;
import com.github.ianrae.dnalparse.parser.ast.ListAssignExp;
import com.github.ianrae.dnalparse.parser.ast.ViaExp;

public class ViaHelper {

    public void adjustTypeIfNeeded(ViaExp via, DStructType dtype, String fieldName) {
        if (via.typeExp == null) {
            DType inner = dtype.getFields().get(fieldName);
            if (inner != null) {
                if (inner instanceof DListType) {
                    adjustTypeIfNeeded(via, (DListType) inner);
                } else {
                    ViaExp tmp = null;
                    IsaRule rule = findIsaRuleForField(dtype, fieldName);
                    if (rule != null) {
                        via.typeExp = new IdentExp(rule.getViaFieldTypeName());
                        via.extraViaExp = new ViaExp(rule.getViaFieldTypeName(), rule.getViaFieldName(), null);
                    } else {
                        via.typeExp = new IdentExp(inner.getCompleteName());
                    }
                }
            }
        }
    }
    public void adjustTypeIfNeeded(ViaExp via, DListType dtype) {
        if (via.typeExp == null) {
            DType inner = dtype.getElementType();
            if (inner != null) {
                via.typeExp = new IdentExp(inner.getCompleteName());
            }
        }
    }
    public void adjustListTypeIfNeeded(ListAssignExp exp, DStructType dtype, String fieldName) {
        ViaExp tmp = null;
        
        for(Exp element: exp.list) {
            if (element instanceof ViaExp) {
                ViaExp via = (ViaExp) element;
                if (tmp == null) {
                    tmp = buildViaForListIsaRule(dtype, fieldName, exp);
                    if (via.typeExp == null && tmp != null) {
                        via.typeExp = tmp.typeExp;
                        via.extraViaExp = tmp;
                    }
                }
                
                this.adjustTypeIfNeeded((ViaExp) element, dtype, fieldName);
            }
        }
    }
    

    private IsaRule findIsaRuleForField(DStructType dtype, String fieldName) {
        for(NRule rule: dtype.getRules()) {
            if (rule instanceof IsaRule) {
                IsaRule isaRule = (IsaRule) rule;
                if (fieldName.equals(isaRule.getFieldName())) {
                    return isaRule;
                }
            }
        }
        return null;
    }

    public ViaExp buildViaForListIsaRule(DStructType dtype, String fieldName, Exp exp) {
        IsaRule rule = findIsaRuleForField(dtype, fieldName);
        if (rule == null) {
            return null;
        }
        
        DType inner = dtype.getFields().get(fieldName);
        if (inner.isShape(Shape.LIST)) {
            ListAssignExp lae = (ListAssignExp) exp;
            Exp elExp = lae.list.get(0); //!!range check later
            ViaExp viaExp = new ViaExp(rule.getViaFieldTypeName(), rule.getViaFieldName(), elExp);
            return viaExp;
        }
        return null;
    }
}
