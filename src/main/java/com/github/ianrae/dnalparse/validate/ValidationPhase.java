package com.github.ianrae.dnalparse.validate;

import java.util.Map;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.SimpleNRuleRunner;
import org.dnal.core.repository.Repository;
import org.dnal.core.repository.WorldListener;

import com.github.ianrae.dnalparse.dnalgenerate.CustomRuleFactory;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.parser.error.ErrorTrackingBase;

public class ValidationPhase extends ErrorTrackingBase {

	private WorldListener world;

	public ValidationPhase(WorldListener world, XErrorTracker et) {
		super(et);
		this.world = world;
	}

	public boolean validate() {

		Map<DType,Repository> repoMap = world.getRepoMap();
		for(DType type : repoMap.keySet()) {
			Repository repo = repoMap.get(type);
			Log.debugLog(String.format("repo %s: %d", type.getName(), repo.size()));
			for(DValue dval : repo.getAll()) {
			    //because of y=x and the type of y might be shape but type of x is circle, then
			    //we can't control the order in which x and y are validated.
			    //we want it to be fully validated (as circle) so we should validate even
			    //if the validation state is not UNKNOWN.
				//					if (! dval.getValState().equals(ValidationState.UNKNOWN)) {
				validateDValue(dval, type);
				//					}
			}
		}

		return areNoErrors();
	}

	public boolean validateDValue(DValue dval, DType type) {
//		SimpleNRuleRunner runner = new SimpleNRuleRunner(ruleL);
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		NRuleContext ctx = new NRuleContext();
		runner.evaluate(dval, ctx);
		chkValErrors(runner);
		return runner.getValidationErrors().isEmpty();
	}

	protected void chkValErrors(SimpleNRuleRunner runner) {
		//propogate
		for(ErrorMessage err: runner.getValidationErrors()) {
			String errType = err.getErrorType().name();
			addError2s("validation error: %s: %s", errType, err.getMessage());
		}
	}

	private void log(String s) {
		Log.log(s);
	}


}