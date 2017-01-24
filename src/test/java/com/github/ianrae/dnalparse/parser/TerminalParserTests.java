package com.github.ianrae.dnalparse.parser;

import static org.junit.Assert.*;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.junit.Test;

import com.github.ianrae.dnalparse.parser.ast.ComparisonRuleExp;
import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.NumberExp;

public class TerminalParserTests {

	@Test
	public void test() {
		Token s = TerminalParser.token("let").from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("let");
		assertEquals("let", s.toString());
	}

	@Test
	public void test2() {
		chkParser("let", "let");
		chkParser(">", ">");
		chkParser(">=", ">=");
	}
	
	private Parser<String> ss() {
		return Parsers.or(TerminalParser.token("let"), TerminalParser.token("end")).retn("abc");
	}
	@Test
	public void test3() {
		String s = ss().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("let");
		assertEquals("abc", s);
	}

//	public static Parser<Exp> rule4() {
//		return Parsers.sequence(Parsers.or(TerminalParser.token(">")), 
//		        Parsers.or(TerminalParser.numberSyntacticParser, TerminalParser.integerSyntacticParser), 
//				(Token optok, String nval) -> new ComparisonRuleExp(null, optok.toString(), Integer.parseInt(nval)));
//	}
//	@Test
//	public void test4() {
//		Exp exp = rule4().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("> 5");
//		assertEquals("> 5", exp.strValue());
//	}
	
    public static Parser<NumberExp> xnumbervalueassign() {
        return Parsers.or(TerminalParser.numberSyntacticParser).
                map(new org.codehaus.jparsec.functors.Map<String, NumberExp>() {
                    @Override
                    public NumberExp map(String arg0) {
                        return new NumberExp(Double.parseDouble(arg0));
                    }
                });
    }
    @Test
    public void test4a() {
        Exp exp = xnumbervalueassign().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("5.2");
        assertEquals("5.2", exp.strValue());
        exp = xnumbervalueassign().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("52");
        assertEquals("52.0", exp.strValue());
    }
	
	
//	public static Parser<Exp> rule5() {
//		return Parsers.sequence(Parsers.or(TerminalParser.token("-")), 
//                Parsers.or(TerminalParser.numberSyntacticParser, TerminalParser.integerSyntacticParser), 
//				(Token optok, String nval) -> new ComparisonRuleExp(null, optok.toString(), Integer.parseInt(nval)));
//	}
//	@Test
//	public void test5() {
//		Exp exp = rule5().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("-5");
//		assertEquals("- 5", exp.strValue());
//	}
//	
	
	
	private void chkParser(String input, String output) {
		Token s = TerminalParser.token(input).from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(input);
		assertEquals(output, s.toString());
	}
}
