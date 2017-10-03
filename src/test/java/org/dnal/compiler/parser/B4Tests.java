package org.dnal.compiler.parser;

import static org.junit.Assert.assertEquals;

import org.dnal.compiler.parser.ast.BooleanExp;
import org.dnal.compiler.parser.ast.ComparisonOrRuleExp;
import org.dnal.compiler.parser.ast.ComparisonRuleExp;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.RuleExp;
import org.dnal.compiler.parser.ast.StringExp;
import org.junit.Test;

public class B4Tests {

	@Test
	public void test1() {
		chkRuleOperand("34", "34");
		chkRuleOperand("-34.5", "-34.5");
		chkRuleOperand("false", "false");
		chkRuleOperand("true", "true");
	}
	@Test
	public void test1a() {
		BooleanExp exp = (BooleanExp) parseRuleOperand("true");
		assertEquals(true, exp.val);
		exp = (BooleanExp) parseRuleOperand("false");
		assertEquals(false, exp.val);
	}
	

	//ruleFn
	@Test
	public void test10() {
		CustomRule exp =  parseRuleFn("a()");
		assertEquals("a", exp.ruleName);
		assertEquals(0, exp.argL.size());
		assertEquals(null, exp.fieldName);
		assertEquals(true, exp.polarity);
	}
	@Test
	public void test10a() {
		CustomRule exp = parseRuleFn("!a()");
		assertEquals("a", exp.ruleName);
		assertEquals(0, exp.argL.size());
		assertEquals(null, exp.fieldName);
		assertEquals(false, exp.polarity);
	}
	@Test
	public void test11() {
		CustomRule exp = parseRuleFn("x.a()");
		assertEquals("a", exp.ruleName);
		assertEquals(0, exp.argL.size());
		assertEquals("x", exp.fieldName);
		assertEquals(true, exp.polarity);
	}
	@Test
	public void test11a() {
		CustomRule exp = parseRuleFn("!x.a()");
		assertEquals("a", exp.ruleName);
		assertEquals(0, exp.argL.size());
		assertEquals("x", exp.fieldName);
		assertEquals(false, exp.polarity);
	}
	
	@Test
	public void test12() {
		CustomRule exp = parseRuleFn("x.a(3,'ab',false)");
		assertEquals("a", exp.ruleName);
		assertEquals(3, exp.argL.size());
		IntegerExp iexp = (IntegerExp) exp.argL.get(0);
		assertEquals(3, iexp.val.intValue());
		StringExp sexp = (StringExp) exp.argL.get(1);
		assertEquals("ab", sexp.val);
		
		assertEquals("x", exp.fieldName);
		assertEquals(true, exp.polarity);
	}
	@Test
	public void test12a() {
		CustomRule exp = parseRuleFn("!x.a(3,'ab',false)");
		assertEquals("a", exp.ruleName);
		assertEquals(3, exp.argL.size());
		IntegerExp iexp = (IntegerExp) exp.argL.get(0);
		assertEquals(3, iexp.val.intValue());
		StringExp sexp = (StringExp) exp.argL.get(1);
		assertEquals("ab", sexp.val);
		assertEquals("x", exp.fieldName);
		assertEquals(false, exp.polarity);
	}
	
	//ruleExpr
	@Test
	public void test20() {
		CustomRule exp =  (CustomRule) parseRuleExpr("a()");
		assertEquals("a", exp.ruleName);
		assertEquals(0, exp.argL.size());
		assertEquals(null, exp.fieldName);
		assertEquals(true, exp.polarity);
	}
	@Test
	public void test20a() {
		ComparisonRuleExp exp =  (ComparisonRuleExp) parseRuleExpr("x < 5");
		assertEquals("< 5", exp.strValue());
		assertEquals(5, exp.val.intValue());
		assertEquals("x", exp.optionalArg.strValue());
	}
	@Test
	public void test20b() {
		ComparisonRuleExp exp =  (ComparisonRuleExp) parseRuleExpr("x < 5.6");
		assertEquals("< 5.60000", exp.strValue());
		assertEquals(5.6, exp.zval, 0.001);
		assertEquals("x", exp.optionalArg.strValue());
	}
	@Test
	public void test20c() {
		ComparisonRuleExp exp =  (ComparisonRuleExp) parseRuleExpr("x < 'ab'");
		assertEquals("< ab", exp.strValue());
		assertEquals("ab", exp.strVal);
		assertEquals("x", exp.optionalArg.strValue());
	}
	@Test
	public void test20d() {
		ComparisonRuleExp exp =  (ComparisonRuleExp) parseRuleExpr("x < z");
		assertEquals("< z", exp.strValue());
		assertEquals("z", exp.identVal);
		assertEquals("x", exp.optionalArg.strValue());
	}
	
	@Test
	public void test21a() {
		ComparisonRuleExp exp =  (ComparisonRuleExp) parseRuleExpr("< 5");
		assertEquals("< 5", exp.strValue());
		assertEquals(5, exp.val.intValue());
		assertEquals(null, exp.optionalArg);
	}
	@Test
	public void test21c() {
		ComparisonRuleExp exp =  (ComparisonRuleExp) parseRuleExpr("< 'ab'");
		assertEquals("< ab", exp.strValue());
		assertEquals("ab", exp.strVal);
		assertEquals(null, exp.optionalArg);
	}
	@Test
	public void test21d() {
		ComparisonRuleExp exp =  (ComparisonRuleExp) parseRuleExpr("< z");
		assertEquals("< z", exp.strValue());
		assertEquals("z", exp.identVal);
		assertEquals(null, exp.optionalArg);
	}
	
	@Test
	public void test22() {
		ComparisonOrRuleExp exp =  (ComparisonOrRuleExp) parseRuleExpr("x < 5 or z < 10");
		assertEquals("< 5 or < 10", exp.strValue());
		assertEquals(5, exp.exp1.val.intValue());
		assertEquals("x", exp.exp1.optionalArg.strValue());
		assertEquals(10, exp.exp2.val.intValue());
		assertEquals("z", exp.exp2.optionalArg.strValue());
	}
	@Test
	public void test22a() {
		ComparisonOrRuleExp exp =  (ComparisonOrRuleExp) parseRuleExpr("< 5 or < 10");
		assertEquals("< 5 or < 10", exp.strValue());
		assertEquals(5, exp.exp1.val.intValue());
		assertEquals(null, exp.exp1.optionalArg);
		assertEquals(10, exp.exp2.val.intValue());
		assertEquals(null, exp.exp2.optionalArg);
	}
	
	@Test
	public void test23() {
		ComparisonOrRuleExp exp =  (ComparisonOrRuleExp) parseRuleExpr("x.a() < 5 or z < 10");
		assertEquals("< 5 or < 10", exp.strValue());
		assertEquals(5, exp.exp1.val.intValue());
		assertEquals("x", exp.exp1.optionalArg.strValue());
		assertEquals(10, exp.exp2.val.intValue());
		assertEquals("z", exp.exp2.optionalArg.strValue());
	}
	@Test
	public void test23a() {
		ComparisonOrRuleExp exp =  (ComparisonOrRuleExp) parseRuleExpr("< x.a() or < 10");
		assertEquals("< 5 or < 10", exp.strValue());
		assertEquals(5, exp.exp1.val.intValue());
		assertEquals(null, exp.exp1.optionalArg);
		assertEquals(10, exp.exp2.val.intValue());
		assertEquals(null, exp.exp2.optionalArg);
	}

	
	
	//--helpers
	private void chkRuleOperand(String src, String expected) {
		Exp ax =  parseRuleOperand(src);
		assertEquals(expected, ax.strValue());
	}
	private Exp parseRuleOperand(String src) {
		Exp ax =  RuleParser.ruleOperand().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
		return ax;
	}
	private CustomRule parseRuleFn(String src) {
		CustomRule ax =  RuleParser.ruleFn().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
		return ax;
	}
	private RuleExp parseRuleExpr(String src) {
		RuleExp ax =  RuleParser.ruleExpr().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
		return ax;
	}
}
