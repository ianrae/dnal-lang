package org.dnal.compiler.core;

import org.junit.Test;

public class ValidateMultiTests extends BaseValidationTests {

	@Test
	public void test() {
		chkOrRule("< 500 or > 1000, < 200", 199, true);
		chkOrRule("< 500 or > 1000, < 200", 200, false);
	}
	@Test
	public void testNoComma() {
		//should fail to compile!
//		chkOrRule("< 500 or > 1000 < 200", 199, true);
//		chkOrRule("fn1(500) fn2(300)", 199, true);
	}
	
	private void chkOrRule(String text, long n, boolean ok) {
		String s = String.format("type Foo long %s end let x Foo = %d", text, n);
		parseAndValidate(s, ok);
	}
	private void parseAndValidate(String input, boolean expected) {
		parseAndValidate(input, expected, "LONG_SHAPE");
	}
	

}
