package org.dnal.compiler.nrule;

import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.RangeExp;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualInt;

//support Long later!!
public class RangeRule extends Custom1Rule<VirtualInt> implements NeedsCustomRule { 
	public CustomRule crule;

	public RangeRule(String name, VirtualInt arg1) {
		super(name, arg1);
	}

	@Override
	protected boolean onEval(DValue dval, NRuleContext ctx) {
		if (crule.argL.size() != 1) {
//			this.addInvalidRuleError(ruleText);
			return false;
		}

		RangeExp rangeExp = (RangeExp) crule.argL.get(0);

		int target = arg1.val;
		int to = rangeExp.to;
		int from = rangeExp.from;
		boolean inRange = (target >= from) && (target < to);
		return inRange;
	}

	@Override
	public void rememberCustomRule(CustomRule exp) {
	    this.polarity = exp.polarity;
		crule = exp;
	}

    @Override
    protected String generateRuleText() {
        return crule.strValue();
    }
	
}