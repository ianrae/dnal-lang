package com.github.ianrae.dnalparse.nrule;

import java.util.List;

import org.dval.DStructHelper;
import org.dval.DStructType;
import org.dval.DType;
import org.dval.DValue;
import org.dval.Shape;
import org.dval.nrule.NRuleBase;
import org.dval.nrule.NRuleContext;

import com.github.ianrae.dnalparse.dnalgenerate.ViaFinder;
import com.github.ianrae.dnalparse.impl.CompilerContext;
import com.github.ianrae.dnalparse.parser.ast.IsaRuleExp;
import com.github.ianrae.dnalparse.parser.ast.StringExp;
import com.github.ianrae.dnalparse.parser.ast.ViaExp;

public class IsaRule extends NRuleBase {
    
    private IsaRuleExp rule;
    private CompilerContext context;

    public IsaRule(String name, IsaRuleExp rule, DType type, CompilerContext context) {
        super(name);
        this.rule = rule;
        this.context = context;
    }
    
    public String getFieldName() {
        return rule.fieldName;
    }
    
    public String getViaFieldName() {
        String ar[] = rule.val.split("\\.");
        String fieldName = ar[1];
        return fieldName;
    }
    public String getViaFieldTypeName() {
        String ar[] = rule.val.split("\\.");
        String typeName = ar[0];
        return typeName;
    }
    
    @Override
    public boolean eval(DValue dval, NRuleContext ctx) {
        if (dval.getType() instanceof DStructType) {
            DStructType structType = (DStructType) dval.getType();
            DType innerType = structType.getFields().get(rule.fieldName);
            if (innerType == null) {
                this.addRuleFailedError(ctx, this.getRuleText());
                return false;
            }
                    
            if (innerType.isShape(Shape.LIST)) {
                DStructHelper helper = new DStructHelper(dval);
                DValue inner = helper.getField(rule.fieldName);
                //eval each element!!!
                int passCount = 0;
                for(DValue element: inner.asList()) {
                    if (checkOne(element, ctx)) {
                        passCount++;
                    }
                }
                return (passCount == inner.asList().size());
            }
        }
        
        DStructHelper helper = new DStructHelper(dval);
        DValue inner = helper.getField(rule.fieldName);
        System.out.println("xxx " + inner.asString());
        
        return checkOne(inner, ctx);
    }
    
    private boolean checkOne(DValue dval, NRuleContext ctx) {
        String typeName = getViaFieldTypeName();
        String fieldName = getViaFieldName();
        
        String value = dval.asString();
        ViaExp via = new ViaExp(typeName, fieldName, new StringExp(value));
        ViaFinder finder = new ViaFinder(context.world, context.registry, context.et);
        List<DValue> list = finder.findMatches(via);
        if (list == null) {
            this.addRuleFailedError(ctx, this.getRuleText());
            return false;
        }
        System.out.println(String.format("vvvv%d", list.size()));
        return list.size() == 1;
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        return true;
    }
}