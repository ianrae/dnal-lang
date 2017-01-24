package org.dnal.core.oldvalidation;
//package org.dval.validation;
//
//import org.dval.DRef;
//import org.dval.DValue;
//import org.dval.Shape;
//import org.dval.validation.rule.RuleRunner;
//
////OLD DON'T USE!!
//public class OLDSimpleVRuleRunner extends BaseVRuleRunner {
//
//	@Override
//	public boolean evaluateSingleRule(DValue dval, VRule rule, RuleContext ctx) {
//		switch(dval.getType().getShape()) {
//		case BOOLEAN:
//			return evalBooleanRule(dval, rule);
//		case INTEGER:
//			return evalIntegerRule(dval, rule);
//		case LIST:
//			return evalListRule(dval, rule);
//		case REF:
//			return evalRefRule(dval, rule);
//		case STRING:
//			return evalStringRule(dval, rule);
//		case STRUCT:
//			return evalStructRule(dval, rule);
//		default:
//			return false;
//		}
//	}
//
//	private boolean evalStructRule(DValue dval, VRule rule) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	private boolean evalStringRule(DValue dval, VRule rule) {
//		return performBasicScalarRule(dval, rule);
//	}
//
//	private boolean evalRefRule(DValue dval, VRule rule) {
//		return evaluateDRefRule(dval, rule);
//	}
//
//	private boolean evalListRule(DValue dval, VRule rule) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	private boolean evalIntegerRule(DValue dval, VRule rule) {
//		int initialCount = valErrorList.size();
//
//		String ruleText = rule.getRule();
//
//		if (ruleText.startsWith("min(")) {
//			doMinimum(ruleText, dval, true);
//		}
//		else if (ruleText.startsWith("max(")) {
//			doMaximum(ruleText, dval, true);
//		} else {
//			performBasicScalarRule(dval, rule);
//		}			
//
//		return initialCount == valErrorList.size();
//	}
//
//	private void doMinimum(String ruleText, DValue dval, boolean b) {
//		ExpresssionParser parser = new ExpresssionParser();
//		Long min = parser.parseFunctionArgLong(ruleText, "min");
//		if (min == null) {
//			addUnknownRuleError(ruleText);
//		} else {
//			Long lval = dval.asLong();
//			if (lval < min) {
//				addRuleFailedError(ruleText);			}
//		}
//	}
//	private void doMaximum(String ruleText, DValue dval, boolean b) {
//		ExpresssionParser parser = new ExpresssionParser();
//		Long max = parser.parseFunctionArgLong(ruleText, "max");
//		if (max == null) {
//			addUnknownRuleError(ruleText);
//		} else {
//			Long lval = dval.asLong();
//			if (lval > max) {
//				addRuleFailedError(ruleText);			}
//		}
//	}
//
//	private boolean evalBooleanRule(DValue dval, VRule rule) {
//		return performBasicScalarRule(dval, rule);
//	}
//
//
//
//	public boolean performBasicScalarRule(DValue dval, VRule rule) {
//		int initialCount = valErrorList.size();
//
//		String ruleText = rule.getRule();
//		switch(ruleText) {
//		case "empty":
//			doEmpty(ruleText, dval, true);
//			break;
//		case "!empty":
//			doEmpty(ruleText, dval, false);
//			break;
//		case "optional":
//			doOptional(ruleText, dval, true);
//			break;
//		case "!optional":
//			doOptional(ruleText, dval, false);
//			break;
//		default:
//			addUnknownRuleError(ruleText);
//			break;
//		}
//
//		return initialCount == valErrorList.size();
//	}
//
//	protected void doEmpty(String ruleText, DValue dval, boolean polarity) {
//		if (! dval.getType().isScalarShape()) {
//			this.addInvalidRuleError(ruleText);
//			return;
//		}
//
//		boolean b = dval.asString().isEmpty();
//		if (b != polarity) {
//			addRuleFailedError(ruleText);			
//		}
//	}
//	protected void doOptional(String ruleText, DValue dval, boolean polarity) {
//		if (! dval.getType().isScalarShape() && ! dval.getType().isShape(Shape.REF)) {
//			this.addInvalidRuleError(ruleText);
//			return;
//		}
//
//		boolean b = (dval.getObject() == null);
//
//		if (b != polarity) {
//			addRuleFailedError(ruleText);			
//		}
//	}
//
//	public boolean evaluateDRefRule(DValue dval, VRule rule) {
//		int initialCount = valErrorList.size();
//
//		String ruleText = rule.getRule();
//		switch(ruleText) {
//		case "optional":
//			doRefCodeOptional(ruleText, dval, true);
//			break;
//		case "!optional":
//			doRefCodeOptional(ruleText, dval, false);
//			break;
//		case "referenceExists": //!optional
//			doOptional(ruleText, dval, false);
//			break;
//		case "!referenceExists": //
//			doOptional(ruleText, dval, true);
//			break;
//		default:
//			addUnknownRuleError(ruleText);
//			break;
//		}
//
//		return initialCount == valErrorList.size();
//	}
//	protected void doRefCodeOptional(String ruleText, DValue dval, boolean polarity) {
//		DRef dref = (DRef) dval;
//
//		//polarity true means optional
//		//b true mean null
//		boolean b = dref.getRefValue() == null;
//
//		if (b != polarity) {
//			addRuleFailedError(ruleText);			
//		}
//	}
//	protected void doMustNotExist(String ruleText, DValue dval) {
//		DRef dref = (DRef) dval;
//
//		if (dref.getObject() != null) {
//			addRuleFailedError(ruleText);			
//		}
//	}
//
//}