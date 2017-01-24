package org.dnal.core.oldvalidation.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.ErrorType;
import org.dnal.core.oldvalidation.ExpresssionParser;
import org.dnal.core.oldvalidation.RuleContext;
import org.dnal.core.oldvalidation.VRule;

public class RuleRunner {
	private List<ZRunner> fnList = new ArrayList<>();
	protected List<ErrorMessage> valErrorList;

	public RuleRunner(List<ErrorMessage> valErrorList) {
		this(valErrorList, null);
	}
	public RuleRunner(List<ErrorMessage> valErrorList, List<ZRunner> ruleL) {
		this.valErrorList = valErrorList;
		
		register(new ZMinRunner(valErrorList, "min"));
		register(new ZMaxRunner(valErrorList, "max"));
		register(new ZEqIntRunner(valErrorList, "eqInt"));
		register(new ZEqIntRunner(valErrorList, "!eqInt"));
		
		register(new ZEmptyRunner(valErrorList, "empty"));
		register(new ZEmptyRunner(valErrorList, "!empty"));

		register(new ZOptionalScalarRunner(valErrorList, "optional"));
		register(new ZOptionalScalarRunner(valErrorList, "!optional"));
		

		register(new ZMinSizeRunner(valErrorList, "minSize"));
		register(new ZMaxSizeRunner(valErrorList, "maxSize"));
		
		if (ruleL != null) {
			for(ZRunner zrunner : ruleL) {
				zrunner.valErrorList = valErrorList;
				register(zrunner);
			}
		}
	}

	private void register(ZRunner zrun) {
		fnList.add(zrun);
	}

	public boolean execute(DValue dval, String ruleText, RuleContext ctx, VRule rule) {
		ExpresssionParser parser = new ExpresssionParser();
		String fnName = parser.parseFunctionName(ruleText);

		if (fnName == null) {
			addUnknownRuleError(ruleText);
			return false;
		} else {
			ZRunner zrun = findMatch(dval, ruleText);

			if (zrun == null) {
				addUnknownRuleError(ruleText);
				return false;
			}
			
			int count = this.getValidationErrors().size();
			if (zrun instanceof ZContextAwareRunner) {
				ZContextAwareRunner zcarun = (ZContextAwareRunner) zrun;
				ctx.setVar2(rule);
				zcarun.execute(dval, ruleText, ctx);
			} else {
				zrun.execute(dval, ruleText);
			}
			return count == this.getValidationErrors().size();
		}
	}

	private ZRunner findMatch(DValue dval, String ruleText) {
		int possibles = 0;
		for(ZRunner zrun: this.fnList) {
			if (ruleText.startsWith(zrun.getFnName())) {
				possibles++;
				if (zrun.willAccept(dval)) {
					return zrun;
				}
			}
		}

		if (possibles > 0) {
			this.addInvalidRuleError(ruleText);
		}

		return null;
	}

	protected void addUnknownRuleError(String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.UNKNOWNRULE, 
				String.format("uknown rule '%s'", ruleText));
		this.valErrorList.add(err);
	}
	protected void addInvalidRuleError(String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.INVALIDRULE, 
				String.format("invalid rule can't be used here '%s'", ruleText));
		this.valErrorList.add(err);
	}
	protected void addRuleFailedError(String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.RULEFAIL, ruleText + "- failed");
		this.valErrorList.add(err);
	}

	public List<ErrorMessage> getValidationErrors() {
		return valErrorList;
	}
}