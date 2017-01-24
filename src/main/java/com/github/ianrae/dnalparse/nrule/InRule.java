package com.github.ianrae.dnalparse.nrule;

import org.dval.DValue;
import org.dval.nrule.NRuleContext;
import org.dval.nrule.virtual.VirtualInt;

import com.github.ianrae.dnalparse.parser.ast.CustomRule;
import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.IntegerExp;


//support Long later!!
public class InRule extends Custom1Rule<VirtualInt> implements NeedsCustomRule { 
	public CustomRule crule;
	
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

	@Override
	public void rememberCustomRule(CustomRule exp) {
	    this.polarity = exp.polarity;
		crule = exp;
	}
}