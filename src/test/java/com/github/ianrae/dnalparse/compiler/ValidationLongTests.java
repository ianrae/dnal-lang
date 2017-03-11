package com.github.ianrae.dnalparse.compiler;

import org.dnal.compiler.et.XErrorTracker;
import org.junit.Test;

public class ValidationLongTests extends BaseValidationTests {

	@Test
	public void testOne() {
		XErrorTracker.logErrors = true;
		chkRule(">", 100, false);
	}
	
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
    public void test1a() {
        long n = 2147483648L;
        chkRule(">", n, 2147483647L, false);
        chkRule(">", n, 2147483648L, false);
        chkRule(">", n, 2147483649L, true);

        chkRule("==", n, 2147483648L, true);
        
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
//		XErrorTracker.logErrors = true;
		chkOrRule("< 500 or > 1000", 599, true);
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
		String s = String.format("type Foo long end let x Foo = 10 let y Foo = x");
		parseAndValidate(s, true);
	}
	
	
	private void chkOrRule(String text, long n, boolean ok) {
		String s = String.format("type Foo long %s end let x Foo = %d", text, n);
		parseAndValidate(s, ok);
	}
    private void chkRule(String op, long n, boolean ok) {
        chkRule(op, 100, n, ok);
    }
	private void chkRule(String op, long ruleVal, long n, boolean ok) {
		String s = String.format("type Foo long %s %d end let x Foo = %d", op, ruleVal, n);
		parseAndValidate(s, ok);
	}
	private void parseAndValidate(String input, boolean expected) {
		parseAndValidate(input, expected, "LONG_SHAPE");
	}

}
