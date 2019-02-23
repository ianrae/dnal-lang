package org.dnal.compiler.nrule;

import java.util.List;

import org.dnal.api.impl.CompilerContext;
import org.dnal.compiler.dnalgenerate.ViaFinder;
import org.dnal.compiler.parser.ast.IsaRuleExp;
import org.dnal.compiler.parser.ast.StringExp;
import org.dnal.compiler.parser.ast.ViaExp;
import org.dnal.compiler.validate.ValidationOptions;
import org.dnal.core.DStructHelper;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRuleBase;
import org.dnal.core.nrule.NRuleContext;

public class IsaRule extends NRuleBase {
	public static final String NAME = "isa";
    
    private IsaRuleExp rule;
    private CompilerContext context;

    public IsaRule(String name, IsaRuleExp rule, DType type, CompilerContext context) {
        super(name);
        this.rule = rule;
        this.context = context;
        this.validationMode = ValidationOptions.VALIDATEMODE_REFS;
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
    	if (! ctx.getValidateOptions().isModeSet(validationMode)) {
    		return true; //don't execute
    	}
    	
        if (dval.getType() instanceof DStructType) {
            DStructType structType = (DStructType) dval.getType();
            DType innerType = structType.getFields().get(rule.fieldName);
            if (innerType == null) {
            	String ruleText = String.format("%s: %s", this.getName(), rule.val);
                this.addRuleFailedError(ctx, ruleText);
                return false;
            }
                    
            if (innerType.isShape(Shape.LIST)) {
                DStructHelper helper = dval.asStruct();
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
        if (inner == null) { //when isa is an optional field and not set
        	return true;
        }
        System.out.println("xxx " + inner.asString());
        
        return checkOne(inner, ctx);
    }
    
    private boolean checkOne(DValue dval, NRuleContext ctx) {
        String typeName = getViaFieldTypeName();
        String fieldName = getViaFieldName();
        
        String value = dval.asString();
        ViaExp via = new ViaExp(0, typeName, fieldName, new StringExp(value));
        ViaFinder finder = new ViaFinder(context.world, context.registry, context.et, null);
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