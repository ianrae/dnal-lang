package org.dnal.core.nrule;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dnal.core.DStructHelper;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.ErrorType;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.ValidationState;
import org.dnal.core.logger.Log;


public class SimpleNRuleRunner  {
    
	private NRuleRunner inner = new NRuleRunnerImpl();
	private Stack<ValidationScorer> stack = new Stack<>();
	private NRuleContext lastCtx;

	public void evaluate(DValue dval, NRuleContext ctx) {
		lastCtx = ctx;
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

		//		this.valErrorList.addAll(ctx.errL);

		stack.pop();
		if (! stack.isEmpty()) {
			Log.log("Val: stack not empty");
		}
	}

	public void innerEvaluate(DValue dval, NRuleContext ctx) {
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

	private void evalScalar(DValue dval, NRuleContext ctx) {
		evalRulesForDValObject(dval, ctx);
	}

	private void evalStruct(DValue dval, NRuleContext ctx) {
		ValidationScorer scorer = new ValidationScorer();
		stack.push(scorer);
//		Map<String,DValue> map = dval.asMap();
		DStructType structType = (DStructType) dval.getType();
		Map<String, DType> map = structType.getFields();
		DStructHelper helper = dval.asStruct();
		
		for(String fieldName : map.keySet()) {
			DValue inner = helper.getField(fieldName);
			
	        //optional is not a rule, but evalauate it like a rule
			if (inner == null) {
			    if (dval.getType() instanceof DStructType) {
			        if (!structType.fieldIsOptional(fieldName)) {
			        	ctx.addErrorZ(ErrorType.RULEFAIL, String.format("fieldName '%s' can't be null. is not optional", fieldName));
			        }
			    }
			} else {
			    innerEvaluate(inner, ctx);
			}
		}
		evalScalar(dval, ctx);
		setCompositeScore(dval);
		stack.pop();
	}

	private void evalList(DValue dval, NRuleContext ctx) {
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

	private void evalRulesForDValObject(DValue dval, NRuleContext ctx) {
		ValidationScorer scorer = stack.peek();

		int passCount = 0;
		int totalNumRules = 0; //for type and base types
		//do rules backwards from current type up to ultimate base type. don't think it matters
		DType dtype = dval.getType();
		while(dtype != null) {
		    for(NRule rule : dtype.getRules()) {
		        totalNumRules++;
		        if (inner.run(dval, rule, ctx)) {
		            passCount++;
		        }
		    }
		    
		    dtype = dtype.getBaseType();
		}

        DValueImpl impl = (DValueImpl) dval;
		boolean b = (passCount == totalNumRules);
		if (b) {
			impl.changeValidState(ValidationState.VALID);
		} else {
			impl.changeValidState(ValidationState.INVALID);
		}
		scorer.score(dval);
	}

	public List<NewErrorMessage> getValidationErrors() {
		return lastCtx.getErrors();
	}
}