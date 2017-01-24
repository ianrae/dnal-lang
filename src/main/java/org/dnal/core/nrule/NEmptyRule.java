package org.dnal.core.nrule;

import org.dnal.core.DValue;
import org.dnal.core.nrule.virtual.VirtualList;
import org.dnal.core.nrule.virtual.VirtualString;

public class NEmptyRule<T> extends NRuleBase {
	public T val1;
	
	public NEmptyRule(String name, T val1) {
		super(name);
		this.val1 = val1;
	}
	@Override
    protected boolean onEval(DValue dval, NRuleContext ctx) {
	    resolveArg(val1, dval, ctx);
		boolean b = false;
		if (val1 instanceof String) {
			String s = (String) val1;
			b = s.isEmpty();
		} else if (val1 instanceof VirtualString) {
			VirtualString vs = (VirtualString) val1;
			b = vs.val.isEmpty();
		} else if (val1 instanceof VirtualList) {
			VirtualList vs = (VirtualList) val1;
			b = vs.val.isEmpty();
		} else {
			addInvalidRuleError(ctx, "unsupported type: " + val1.getClass().getSimpleName());
		}
		
		//add list later!!
		return b;
	}
}