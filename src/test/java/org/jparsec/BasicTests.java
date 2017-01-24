package org.jparsec;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.junit.Test;

public class BasicTests {

	@Test
	public void test() {
		Parser<List<String>> numbers = Scanners.INTEGER.sepBy(Scanners.isChar(','));
		assertEquals(Arrays.asList("1", "2", "3"), numbers.parse("1,2,3"));
	}
	
	@Test
	public void test2() {
		Terminals operators = Terminals.operators(","); // only one operator supported so far
		Parser<?> integerTokenizer = Terminals.IntegerLiteral.TOKENIZER;
		Parser<String> integerSyntacticParser = Terminals.IntegerLiteral.PARSER;
		Parser<?> ignored = Parsers.or(Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES);
		Parser<?> tokenizer = Parsers.or(operators.tokenizer(), integerTokenizer); // tokenizes the operators and integer
		Parser<List<String>> integers = integerSyntacticParser.sepBy(operators.token(","))
		    .from(tokenizer, ignored.skipMany());
		assertEquals(Arrays.asList("1", "2", "3"), integers.parse("1,  /*this is comment*/2, 3"));		
	}

	@Test
	public void test3() {
		Terminals operators = Terminals.operators(",", ";"); 
		Parser<?> integerTokenizer = Terminals.Identifier.TOKENIZER;
		Parser<String> integerSyntacticParser = Terminals.Identifier.PARSER;
		Parser<?> ignored = Parsers.or(Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES);
		Parser<?> tokenizer = Parsers.or(operators.tokenizer(), integerTokenizer); // tokenizes the operators and integer
		
		Parser<List<String>> integers = integerSyntacticParser.sepBy(operators.token(","))
		    .from(tokenizer, ignored.skipMany());
		assertEquals(Arrays.asList("a1", "b2", "c3"), integers.parse("a1,  /*this is comment*/b2, c3"));		
	}
	
	@Test
	public void test4() {
		Terminals operators = Terminals.operators("var", "end", "="); 
		Parser<?> identTokenizer = Terminals.Identifier.TOKENIZER;
		Parser<String> identSyntacticParser = Terminals.Identifier.PARSER;
		Parser<?> ignored = Parsers.or(Scanners.JAVA_LINE_COMMENT, Scanners.WHITESPACES);
		Parser<?> tokenizer = Parsers.or(operators.tokenizer(), identTokenizer); // tokenizes the operators and integer
		
		Parser<String> p1 = Parsers.sequence(operators.token("var"), identSyntacticParser).next(operators.token("=")).next(identSyntacticParser);;
		Parser<String> p = p1.from(tokenizer, ignored.skipMany());
		assertEquals("b", p.parse("var a = b//cmt"));
	}
}
