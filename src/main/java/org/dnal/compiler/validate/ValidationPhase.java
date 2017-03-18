package org.dnal.compiler.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.SimpleNRuleRunner;
import org.dnal.core.repository.Repository;
import org.dnal.core.repository.World;
import org.dnal.core.repository.WorldListener;

public class ValidationPhase extends ErrorTrackingBase {

	private WorldListener world;
	private Map<NRule,Integer> alreadyRunMap = new HashMap<>();
	private ValidationOptions validateOptions;
	private List<DValue> futureValues = new ArrayList<>();


	public ValidationPhase(WorldListener world, XErrorTracker et, ValidationOptions validateOptions) {
		super(et);
		this.world = world;
		this.validateOptions = validateOptions;
	}

	public boolean validate() {
		/*
		 * Note. i think the reason we validate by walking the repos is to avoid infinite loops.
		 * If we traversed each top-level dval could get into an infinite loop, and also
		 * could end up validating some values multiple times
		 */

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
				String varName = "unknown";
				if (world instanceof World) {
					World worldObj = (World) world;
					varName = worldObj.findTopValueValueName(dval);
					if (varName == null) {
						varName = "unk";
					}
				}
				validateDValue(varName, dval, type);
				//					}
			}
		}

		return areNoErrors();
	}

	public boolean validateDValue(String varName, DValue dval, DType type) {
		SimpleNRuleRunner runner = new SimpleNRuleRunner();
		
		//pass in alreadyRunMap so we can avoid executing UniqueRule instances more than once
		NRuleContext ctx = new NRuleContext(getET(), alreadyRunMap, validateOptions, futureValues);
		ctx.setCurrentVarName(varName);
		runner.evaluate(dval, ctx);
		return runner.getValidationErrors().isEmpty();
	}

	public List<DValue> getFutureValues() {
		return futureValues;
	}


}