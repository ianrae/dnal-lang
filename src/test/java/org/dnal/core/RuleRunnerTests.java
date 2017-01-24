package org.dnal.core;
//package org.dval;
//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//
//import org.dval.validation.RuleContext;
//import org.dval.validation.VRule;
//import org.dval.validation.rule.RuleRunner;
//import org.junit.Before;
//import org.junit.Test;
//
//public class RuleRunnerTests extends BaseDValTest {
//
//	@Test
//	public void testMin() {
//		runIntRuleOK("min(100)", 100);
//		runIntRuleOK("min(100)", 104);
//		runIntRuleFail("min(100)", 99);
//	}
//	@Test
//	public void testMax() {
//		runIntRuleOK("max(100)", 99);
//		runIntRuleOK("max(100)", 100);
//		runIntRuleFail("max(100)", 101);
//	}
//
//	@Test
//	public void testEmpty() {
//		runStringRuleOK("empty()", "");
//		runStringRuleFail("empty()", "abc");
//	}
//	@Test
//	public void testMinSize() {
//		runStringRuleOK("minSize(4)", "abcd");
//		runStringRuleOK("minSize(4)", "abcde");
//		runStringRuleFail("minSize(4)", "abc");
//	}
//	@Test
//	public void testMaxSize() {
//		runStringRuleOK("maxSize(4)", "abc");
//		runStringRuleOK("maxSize(4)", "abcd");
//		runStringRuleFail("maxSize(4)", "abcde");
//	}
//
//
//	@Test
//	public void testOptionalScalar() {
//		runNullScalarRuleOK("optional()");
//		runIntRuleOK("optional()", 44);
//
//		runNullScalarRuleFail("!optional()");
//		runIntRuleOK("!optional()", 44);
//	}
//	
//	@Test
//	public void testOptionalRef() {
//		DType type = registry.getType(BuiltInTypes.REF_SHAPE);
//		DStructType addrType = personHelper.buildAddressType(registry);
//		DStructType personType = personHelper.buildPersonType(registry, type);
//
//		DValue addr = personHelper.buildAddress(registry, addrType);
//		DRef dref = personHelper.buildRef(registry, addr.getType(), "Person.address", "101");
//		DValue person = personHelper.buildPerson(registry, personType, dref);
//		
//		assertEquals("code", dref.getFieldName());
//		assertNotNull(dref.getObject());
//		
//		RuleRunner runner = new RuleRunner(new ArrayList<ValidationError>());
//		runAndChkErrors(runner, dref, "referenceExists()", 0);
//
//		runner = new RuleRunner(new ArrayList<ValidationError>());
//		runAndChkErrors(runner, dref, "!referenceExists()", 1);
//	}
//
//	//---------
//	PersonHelper personHelper;
//	
//	@Before
//	public void init() {
//		super.init();
//		personHelper = new PersonHelper(registry, world);
//	}
//	
//	
//	private void runNullScalarRuleOK(String ruleText) {
//		RuleRunner runner = new RuleRunner(new ArrayList<ValidationError>());
//		DValue dval = new DValue(registry.getType(BuiltInTypes.INTEGER_SHAPE), null);
//		runAndChkErrors(runner, dval, ruleText, 0);
//	}
//	private void runNullScalarRuleFail(String ruleText) {
//		RuleRunner runner = new RuleRunner(new ArrayList<ValidationError>());
//		DValue dval = new DValue(registry.getType(BuiltInTypes.INTEGER_SHAPE), null);
//		runAndChkErrors(runner, dval, ruleText, 1);
//	}
//	private void runIntRuleOK(String ruleText, int intVal) {
//		RuleRunner runner = new RuleRunner(new ArrayList<ValidationError>());
//		DValue dval = buildIntVal(registry, intVal);
//		runAndChkErrors(runner, dval, ruleText, 0);
//	}
//	private void runIntRuleFail(String ruleText, int intVal) {
//		RuleRunner runner = new RuleRunner(new ArrayList<ValidationError>());
//		DValue dval = buildIntVal(registry, intVal);
//		runAndChkErrors(runner, dval, ruleText, 1);
//	}
//	private void runStringRuleOK(String ruleText, String s) {
//		RuleRunner runner = new RuleRunner(new ArrayList<ValidationError>());
//		DValue dval = buildStringVal(registry, s);
//		runAndChkErrors(runner, dval, ruleText, 0);
//	}
//	private void runStringRuleFail(String ruleText, String s) {
//		RuleRunner runner = new RuleRunner(new ArrayList<ValidationError>());
//		DValue dval = buildStringVal(registry, s);
//		runAndChkErrors(runner, dval, ruleText, 1);
//	}
//	
//	private void runAndChkErrors(RuleRunner runner, DValue dval, String ruleText, int expected) {
//		RuleContext ctx = new RuleContext();
//		VRule vrule = null; //fix later!!
//		boolean b = runner.execute(dval, ruleText, ctx, vrule);
//		chkErrors(b, runner, expected);
//	}
//
//	private void chkErrors(boolean b, RuleRunner runner, int expected) {
//		int numErrs = runner.getValidationErrors().size();
//		assertEquals((numErrs == 0), b);
//
//		for(ValidationError err: runner.getValidationErrors()) {
//			log(err.getMessage());
//		}
//		assertEquals(expected, numErrs);
//	}
//
//}
