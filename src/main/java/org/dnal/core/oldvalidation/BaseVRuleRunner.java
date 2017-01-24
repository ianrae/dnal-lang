package org.dnal.core.oldvalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.ErrorMessage;
import org.dnal.core.ErrorType;
import org.dnal.core.ValidationState;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.ValidationScorer;

public abstract class BaseVRuleRunner implements DValueValidator {
	protected List<ErrorMessage> valErrorList = new ArrayList<>();
	private Stack<ValidationScorer> stack = new Stack<>();
	public abstract boolean evaluateSingleRule(DValue dval, NRule rule, RuleContext ctx);

	@Override
	public void evaluate(DValue dval, RuleContext ctx) {
		ValidationScorer scorer = new ValidationScorer();
		stack.push(scorer);
		switch(dval.getType().getShape()) {
		case LIST:
			evalList(dval, ctx);
			break;
		case STRUCT:
			evalStruct(dval, ctx);
			break;
		default:
			evalScalar(dval, ctx);
			break;
		}
		stack.pop();
		if (! stack.isEmpty()) {
			Log.log("Val: stack not empty");
		}
	}

	public void innerEvaluate(DValue dval, RuleContext ctx) {
		switch(dval.getType().getShape()) {
		case LIST:
			evalList(dval, ctx);
			break;
		case STRUCT:
			evalStruct(dval, ctx);
			break;
		default:
			evalScalar(dval, ctx);
			break;
		}
	}

	private void evalScalar(DValue dval, RuleContext ctx) {
		evalRulesForDValObject(dval, ctx);
	}

	private void evalStruct(DValue dval, RuleContext ctx) {
		ValidationScorer scorer = new ValidationScorer();
		stack.push(scorer);
		Map<String,DValue> map = dval.asMap();
		for(String fieldName : map.keySet()) {
			DValue inner = map.get(fieldName);
			innerEvaluate(inner, ctx);
		}
		evalScalar(dval, ctx);
		setCompositeScore(dval);
		stack.pop();
	}

	private void evalList(DValue dval, RuleContext ctx) {
		ValidationScorer scorer = new ValidationScorer();
		stack.push(scorer);
		List<DValue> list = dval.asList();
		for(DValue inner : list) {
			innerEvaluate(inner, ctx);
		}

		evalScalar(dval, ctx);
		setCompositeScore(dval);
		stack.pop();
	}

	private void setCompositeScore(DValue dval) {
		ValidationScorer scorer = stack.peek();
        DValueImpl impl = (DValueImpl) dval;

		switch(dval.getValState()) {
		case UNKNOWN:
			break;
		case VALID:
			impl.changeValidState(scorer.allValid() ? ValidationState.VALID : 
				(scorer.someInvalid() ? ValidationState.INVALID : ValidationState.UNKNOWN));
		break;
		case INVALID:
			impl.changeValidState(scorer.someUnknown() ? ValidationState.UNKNOWN : ValidationState.INVALID);
		break;
		default:
			break;
		}
	}

	private void evalRulesForDValObject(DValue dval, RuleContext ctx) {
		ValidationScorer scorer = stack.peek();
		DType type = dval.getType();
		boolean addNotOptionalRule = false;
		if (! type.hasRules()) {
			addNotOptionalRule = true;
		}

		int passCount = 0;
		boolean optionalSeen = false; //either optional or !optionals
		for(NRule rule : dval.getType().getRules()) {
			if (evaluateSingleRule(dval, rule, ctx)) {
				passCount++;
			}
			
			String s = rule.getName().trim();
			if (s.contains("optional")) {
				optionalSeen = true;
			}
		}

//		//by default all values are not optional
//		if (! optionalSeen || addNotOptionalRule) {
//			if (dval.getType().isScalarShape() || dval.getType().isShape(Shape.REF)) {
//				NRule xrule = new VRule("!optional");
//				if (evaluateSingleRule(dval, xrule, ctx)) {
//					//don't increment passcount
//				}
//			}
//		}
		
        DValueImpl impl = (DValueImpl) dval;
		boolean b = (passCount == dval.getType().getRules().size());
		if (b) {
			impl.changeValidState(ValidationState.VALID);
		} else {
			impl.changeValidState(ValidationState.INVALID);
		}
		scorer.score(dval);
	}


	protected void addUnknownRuleError(String ruleText) {
		ErrorMessage err = new ErrorMessage(ErrorType.UNKNOWNRULE, 
				String.format("uknown rule '%s'", ruleText));
		this.valErrorList.add(err);
	}
	protected void addUnknownFieldError(String ruleText, String fieldName) {
		ErrorMessage err = new ErrorMessage(ErrorType.UNKNOWNRULE, 
				String.format("uknown field '%s' in rule '%s'", fieldName, ruleText));
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