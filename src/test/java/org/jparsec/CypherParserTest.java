package org.jparsec;


import org.codehaus.jparsec.error.ParserException;
import org.junit.Test;

public class CypherParserTest {

	private final CypherParser parser = new CypherParser();

//	@Test
//	public void parsesIdentifier() throws Exception {
//		assertThat(new Identifier("joe"), equalTo(parser.parse("joe")));
//		assertThat(new Identifier("joe67_"), equalTo(parser.parse("joe67_")));
//	}
//
	@Test(expected = ParserException.class)
	public void rejectsIdentifierStartingWithANumber() throws Exception {
		parser.parse("67joe");
	}

//	@Test
//	public void parsesFunctionCall() throws Exception {
//		assertThat(new Function(new Identifier("foo")), equalTo(parser.parse("foo()")));
//		assertThat(new Function(new Identifier("foo"), new Identifier("bar")), equalTo(parser.parse("foo(bar)")));
//		assertThat(new Function(new Identifier("foo"), new Identifier("bar"), new Identifier("baz"), new Identifier("qix")), equalTo(parser.parse("foo(bar,baz,qix)")));
//		Function fn = new Function(new Identifier("foo"), new Identifier("bar"),new Function(new Identifier("baz"), new Identifier("qix")));
//		assertThat(fn, equalTo(parser.parse("foo(bar,baz(qix))")));
//	}
	
	
//	@Test
//	public void test1() throws Exception {
//		assertThat(new Identifier("xxtype"), equalTo(parser.parse("xxtype")));
//	}
//	@Test
//	public void test2() throws Exception {
//		DNType fn = new DNType(new Identifier("foo"), new Identifier("bar"),new Identifier("qix"));
//		assertThat(fn, equalTo(parser.parse("type foo { bar, qix } end")));
//	}
	
}
