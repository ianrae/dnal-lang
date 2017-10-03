package org.dnal.compiler.parser;

import static org.junit.Assert.assertEquals;

import org.dnal.compiler.parser.ast.BooleanExp;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IdentExp;
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
	
	
	//--
	private void chkRuleOperand(String src, String expected) {
		Exp ax =  parseRuleOperand(src);
		assertEquals(expected, ax.strValue());
	}
	private Exp parseRuleOperand(String src) {
		Exp ax =  RuleParser.ruleOperand().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
		return ax;
	}
}
