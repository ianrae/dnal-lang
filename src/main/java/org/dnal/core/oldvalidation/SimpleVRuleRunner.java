package org.dnal.core.oldvalidation;
//package org.dval.validation;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.dval.DStructHelper;
//import org.dval.DValue;
//import org.dval.Shape;
//import org.dval.ValidationState;
//import org.dval.validation.rule.RuleRunner;
//import org.dval.validation.rule.ZRunner;
//
//public class SimpleVRuleRunner extends BaseVRuleRunner {
//	private RuleRunner ruleRunner;
//	private List<ZRunner> ruleL = new ArrayList<>();
//
//	public SimpleVRuleRunner() {
//		ruleRunner = new RuleRunner(this.valErrorList, ruleL);
//	}
//	public SimpleVRuleRunner(List<ZRunner> ruleL) {
//		this.ruleL = ruleL;
//		ruleRunner = new RuleRunner(this.valErrorList, ruleL);
//	}
//	
//	
//	
//	@Override
//	public boolean evaluateSingleRule(DValue dval, VRule rule, RuleContext ctx) {
//		if (rule instanceof CompositeRule) {
//			return evalCompositeRule(dval, (CompositeRule)rule, ctx);
//		}
//		String ruleText = rule.getRule();
//		if (! ruleText.endsWith(")")) {
//			ruleText += "()";
//		}
//		
//		if (dval.getType().isShape(Shape.STRUCT)) {
//			return doStructRules(dval, rule, ruleText, ctx);
//		}
//		
//		return ruleRunner.execute(dval, ruleText, ctx, rule);
//	}
//
//	private boolean evalCompositeRule(DValue dval, CompositeRule rule, RuleContext ctx) {
//		if (rule.getOp().equals("or")) {
//			boolean b1 = this.evaluateSingleRule(dval, rule.getLeft(), ctx);
//			if (b1) {
//				return true;
//			}
//			boolean b2 = this.evaluateSingleRule(dval, rule.getRight(), ctx);
//			return (b1 || b2);
//		} else if (rule.getOp().equals("and")) {
//			boolean b1 = this.evaluateSingleRule(dval, rule.getLeft(), ctx);
//			if (!b1) {
//				return false;
//			}
//			boolean b2 = this.evaluateSingleRule(dval, rule.getRight(), ctx);
//			return (b1 && b2);
//		} else {
//			this.addInvalidRuleError(rule.getRule());
//			return false;
//		}
//	}
//
//	private boolean doStructRules(DValue dval, VRule rule, String ruleText, RuleContext ctx) {
//		ExpresssionParser parser = new ExpresssionParser();
//		String fnName = parser.parseFunctionName(ruleText);
//		String fieldName = parser.parseFunctionFirstArg(ruleText, fnName);
//		
//		DStructHelper helper = dval.asStruct();
//		DValue field = helper.getField(fieldName);
//		
//		if (field == null) {
//			this.addUnknownFieldError(ruleText, fieldName);
//			return false;
//		} else {
//			String arg2 = parser.parseFunctionSecondArg(ruleText, fnName);
//			String simplifiedRule = String.format("%s(%s)", fnName, arg2);
//			VRule rule2 = new VRule(simplifiedRule);
//			boolean b = evaluateSingleRule(field, rule2, ctx);
//			setScoreForMigratedRule(field, b);
//			return b;
//		}
//	}
//	
//	private void setScoreForMigratedRule(DValue dval, boolean b) {
//		dval.changeValidState(b ? ValidationState.VALID : ValidationState.INVALID);
//	}
//	
//
//}