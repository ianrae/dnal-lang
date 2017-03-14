package org.dnal.compiler.core;

import org.junit.Test;

public class ValidationTests extends BaseValidationTests {

	@Test
	public void test1() {
		chkRule(">", 99, false);
		chkRule(">", 100, false);
		chkRule(">", 101, true);
		
		chkRule(">=", 99, false);
		chkRule(">=", 100, true);
		chkRule(">=", 101, true);
		
		chkRule("<", 99, true);
		chkRule("<", 100, false);
		chkRule("<", 101, false);
		
		chkRule("<=", 99, true);
		chkRule("<=", 100, true);
		chkRule("<=", 101, false);
	}
	
	@Test
	public void test2() {
		chkRule("==", 99, false);
		chkRule("==", 100, true);
		
		chkRule("!=", 99, true);
		chkRule("!=", 100, false);
	}
	
	@Test
	public void testOr() {
		chkOrRule("< 500 or > 1000", 99, true);
	}
	@Test
	public void testOrFail() {
		chkOrRule("< 500 or > 1000", 555, false);
	}
	@Test
	public void testAnd() {
		chkOrRule("< 500 and > 10", 99, true);
	}
	@Test
	public void testAndFail() {
		chkOrRule("< 500 and > 10", 4, false);
	}
	
	@Test
	public void testAssignOtherVar() {
		expected = 3;
		String s = String.format("type Foo int end let x Foo = 10 let y Foo = x");
		parseAndValidate(s, true);
	}
	
	
	private void chkOrRule(String text, int n, boolean ok) {
		String s = String.format("type Foo int %s end let x Foo = %d", text, n);
		parseAndValidate(s, ok);
	}
	private void chkRule(String op, int n, boolean ok) {
		String s = String.format("type Foo int %s 100 end let x Foo = %d", op, n);
		parseAndValidate(s, ok);
	}
	private void parseAndValidate(String input, boolean expected) {
		parseAndValidate(input, expected, "INTEGER_SHAPE");
	}

}
