package org.dnal.compiler.parser;

import static org.junit.Assert.assertEquals;

import org.codehaus.jparsec.error.ParserException;
import org.dnal.compiler.parser.ast.ComparisonOrRuleExp;
import org.dnal.compiler.parser.ast.ComparisonRuleExp;
import org.dnal.compiler.parser.ast.FullTypeExp;
import org.junit.Test;

public class B3Tests {

	@Test
	public void test13a() {
		FullTypeExp ax = (FullTypeExp)FullParser. parse02("type X int < 0 or > 5, < 10 end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(2, ax.ruleList.size());
		ComparisonOrRuleExp exp = (ComparisonOrRuleExp) ax.ruleList.get(0);
		assertEquals("< 0", exp.exp1.strValue());
		assertEquals("> 5", exp.exp2.strValue());
		ComparisonRuleExp exp2 = (ComparisonRuleExp) ax.ruleList.get(1);
	}
	@Test(expected=ParserException.class)
	public void test13aNoComma() {
		FullTypeExp ax = (FullTypeExp)FullParser. parse02("type X int < 0 or > 5 < 10 end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(2, ax.ruleList.size());
		ComparisonOrRuleExp exp = (ComparisonOrRuleExp) ax.ruleList.get(0);
		assertEquals("< 0", exp.exp1.strValue());
		assertEquals("> 5", exp.exp2.strValue());
		ComparisonRuleExp exp2 = (ComparisonRuleExp) ax.ruleList.get(1);
	}
}
