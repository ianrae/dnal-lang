package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualInt;


//support Long later!!
public class InRule extends Custom1RuleBase<VirtualInt> { 
	
	public InRule(String name, VirtualInt arg1) {
		super(name, arg1);
	}

	@Override
	protected boolean onEval(DValue dval, NRuleContext ctx) {
		
		boolean found = false;
		for(Exp exp: crule.argL) {
			IntegerExp iexp = (IntegerExp) exp;
			if (arg1.val == iexp.val) {
				found = true;
				break;
			}
		}		
		return found;
	}
}