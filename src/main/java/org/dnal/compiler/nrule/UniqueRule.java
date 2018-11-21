package org.dnal.compiler.nrule;

import java.util.List;

import org.codehaus.jparsec.functors.Pair;
import org.dnal.api.impl.CompilerContext;
import org.dnal.compiler.dnalgenerate.ViaFinder;
import org.dnal.compiler.validate.ValidationOptions;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.ErrorType;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRuleBase;
import org.dnal.core.nrule.NRuleContext;

public class UniqueRule extends NRuleBase {
    
    private String fieldName;
    private CompilerContext context;
	private List<Pair<String, DValue>> pendingL; //transaction items. not yet added to repo but need validation of uniqueness

    public UniqueRule(String name, String fieldName, DType type, CompilerContext context) {
        super(name);
        this.fieldName = fieldName;
        this.context = context;
        this.validationMode = ValidationOptions.VALIDATEMODE_REFS;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public boolean eval(DValue dval, NRuleContext ctx) {
    	if (! ctx.getValidateOptions().isModeSet(validationMode)) {
    		return true; //don't execute
    	}
    	
        DStructType structType = (DStructType) dval.getType();
        DType innerType = structType.getFields().get(fieldName);
        if (innerType == null) {
            this.addRuleFailedError(ctx, this.getRuleText());
            return false;
        }
        
        boolean pass = false;
        switch(innerType.getShape()) {
        case INTEGER:
        case LONG:
        case STRING:
            pass = checkRule(structType, ctx);
            break;
        default:
            this.addRuleFailedError(ctx, this.getRuleText() + " - can only be used on fields of type int,long, or string");
            break;
        }
        
        return pass;
    }
    

    private boolean checkRule(DStructType structType, NRuleContext ctx) {
    	if (ctx.haveAlreadyRun(this)) {
    		return true;
    	}
    	ctx.addToAlreadyRunMap(this);
    	
        ViaFinder finder = new ViaFinder(context.world, context.registry, context.et, null);
        finder.setPendingL(this.pendingL); //can be null
        boolean b = finder.calculateUnique(structType, fieldName);
        Log.debugLog("UniqueRule executed %b", b);
    	
		if (!b) {
			String s = String.format("%s: %s", this.getName(), this.getRuleText());
			ctx.addErrorWithField(ErrorType.RULEFAIL, s, fieldName);
		}
        
        return b;
    }

    @Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
        return true;
    }

	public void setContext(CompilerContext context) {
		this.context = context;
	}

	public void setPendingL(List<Pair<String, DValue>> pendingL) {
		this.pendingL = pendingL;
	}
}