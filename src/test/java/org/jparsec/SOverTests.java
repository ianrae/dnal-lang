package org.jparsec;

import static org.junit.Assert.*;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.functors.Map;
import org.junit.Test;

public class SOverTests {

	private static class FieldNode {
		final String text;

		public FieldNode(String text) {
			this.text = text;
		}	
	}

	// lexing
	static final Terminals OPERATORS = Terminals.operators("=", "OR", "AND", "NOT", "(", ")", "IN", "[", "]", ",", "<>");
	static final Parser<String> FIELD_NAME_TOKENIZER = Terminals.Identifier.TOKENIZER.source();
	static final Parser<?> QUOTED_STRING_TOKENIZER = Terminals.StringLiteral.SINGLE_QUOTE_TOKENIZER.or(Terminals.StringLiteral.DOUBLE_QUOTE_TOKENIZER);
	static final Terminals TERMINALS = Terminals.caseSensitive(new String[] { "=", "(", ")", "[", "]", ",", "<>" }, new String[] { "OR", "AND", "NOT", "IN" });
	static final Parser<?> TOKENIZER = Parsers.or(TERMINALS.tokenizer(), QUOTED_STRING_TOKENIZER);


	private static Parser<FieldNode> fieldNodeParser = Parsers.sequence(Terminals.fragment(Tokens.Tag.IDENTIFIER).map(new Map<String, FieldNode>() {
		@Override
		public FieldNode map(String from) {
			String fragment = from;
			return new FieldNode(fragment);
		}
	})).cast();

	public static Parser<FieldNode> parser = fieldNodeParser.from(TOKENIZER, Scanners.WHITESPACES);


//	@Test
//	public void test_tokenizer() {
//		Object result = Parsers.or(TOKENIZER.cast(), Scanners.WHITESPACES.cast()).parse("foo='abc' AND bar<>'def' OR (biz IN ['a', 'b', 'c'] AND NOT baz = 'foo')");
//		assertEquals("[foo, =, abc, null, AND, null, bar, <>, def, null, OR, null, (, biz, null, IN, null, [, a, ,, null, b, ,, null, c, ], null, AND, null, NOT, null, baz, null, =, null, foo, )]", result.toString());
//	}

	@Test
	public void test_parser() throws Exception {
		FieldNode foo = parser.parse("foo");
		assertEquals(foo.text, "foo");
	}
//	@Test
//	public void test1() throws Exception {
//		FieldNode foo = parser.parse("foo='abc'");
//		assertEquals(foo.text, "foo");
//	}

	@Test
	public void test2() throws Exception {
		String s = FIELD_NAME_TOKENIZER.parse("AND");
		assertEquals(s, "AND");
	}

}
