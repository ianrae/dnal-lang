package org.jparsec;

import static org.junit.Assert.assertEquals;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.junit.Test;

public class SpecialTests {

	private interface Expression {
	}

	private static class ReadonlyExpression implements Expression {
		public String s;
		public ReadonlyExpression(String s) {
			this.s = s;
		}
	}
	public static class VarExpression implements Expression {
		public String s;
		
		public VarExpression(String s) {
			this.s = s;
		}
	}
	public static class IdentExpression implements Expression {
		public String s;
		
		public IdentExpression(String s) {
			this.s = s;
		}
	}
	public static class ValueExpression implements Expression {
		public Integer nVal;
		
		public ValueExpression(String s) {
			this.nVal = Integer.parseInt(s);
		}
	}
	public static class FullExpression implements Expression {
		public IdentExpression identExpr;
		public ValueExpression valueExpr;
		
		public FullExpression(IdentExpression identExpr, ValueExpression valueExpr) {
			this.identExpr = identExpr;
			this.valueExpr = valueExpr;
		}
	}
	
	
	public static class DoubleExpression implements Expression {
		public String s;
		public String s2;
		
		public DoubleExpression(String s, String s2) {
			this.s = s;
			this.s2 = s2;
		}
	}

	//low-level grammar
	private static final Terminals OPERATORS =
			Terminals.operators("=", "readonly", "var");

	//for integers and identifiers we need a PARSER and a TOKENIZER for each
	//here are the parsers
	public static Parser<String> identSyntacticParser = Terminals.Identifier.PARSER;
	public static Parser<String> integerSyntacticParser = Terminals.IntegerLiteral.PARSER;
	
	//and here are the tokenizers, combined into our single tokenizer
	static final Parser<?> TOKENIZER = Parsers.or(
					OPERATORS.tokenizer(),
					Terminals.IntegerLiteral.TOKENIZER, 
					Terminals.Identifier.TOKENIZER, 
					Terminals.Identifier.TOKENIZER);

	static final Parser<Void> IGNORED = Parsers.or(
			Scanners.JAVA_LINE_COMMENT,
			Scanners.JAVA_BLOCK_COMMENT,
			Scanners.WHITESPACES).skipMany();	
	
	private static Parser<String> readonly() {
		return OPERATORS.token("readonly").retn("readonly");
	}
	private static Parser<String> var() {
		return OPERATORS.token("var").retn("var");
	}
	private static Parser<String> eq() {
		return OPERATORS.token("=").retn("=");
	}

	//high-level grammar

	private static Parser<ReadonlyExpression> readonlyExpression() {
		return Parsers.or(readonly())
				.map(new org.codehaus.jparsec.functors.Map<String, ReadonlyExpression>() {
					@Override
					public ReadonlyExpression map(String arg0) {
						return new ReadonlyExpression(arg0);
					}
				});
	}
	private static Parser<VarExpression> varExpression() {
		return Parsers.or(var())
				.map(new org.codehaus.jparsec.functors.Map<String, VarExpression>() {
					@Override
					public VarExpression map(String arg0) {
						return new VarExpression(arg0);
					}
				});
	}
	
	public static Parser<IdentExpression> ident() {
		return Parsers.or(identSyntacticParser).
				map(new org.codehaus.jparsec.functors.Map<String, IdentExpression>() {
					@Override
					public IdentExpression map(String arg0) {
						return new IdentExpression(arg0);
					}
				});
	}
	
	//'=' followed by an integer value
	private static Parser<ValueExpression> assignment() {
		return Parsers.sequence(eq(), integerSyntacticParser).
				map(new org.codehaus.jparsec.functors.Map<String, ValueExpression>() {
					@Override
					public ValueExpression map(String arg0) {
						return new ValueExpression(arg0);
					}
				});
	}
	
	
	//var x = 5
	private static Parser<FullExpression> full() {
		return Parsers.sequence(var(), Parsers.sequence(ident(), assignment(), FullExpression::new));
	}

	static final FullExpression parse(String input) {
		Parser<FullExpression> grammar = full();
		FullExpression exp = grammar.from(TOKENIZER, IGNORED).parse(input);
		return exp;
	}


	@Test
	public void test4() {
		FullExpression exp = parse("var x = 50");
		assertEquals("x", exp.identExpr.s);
		assertEquals(50, exp.valueExpr.nVal.intValue());
	}
	
	
	//you can test individual pieces of the grammar
	@Test
	public void test5() {
		ValueExpression exp = (ValueExpression) assignment().from(TOKENIZER, IGNORED).parse("= 5");
		assertEquals(5, exp.nVal.intValue());
	}
	
}
