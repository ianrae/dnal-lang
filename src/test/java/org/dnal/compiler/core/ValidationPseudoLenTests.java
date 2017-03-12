package org.dnal.compiler.core;

import org.junit.Test;

public class ValidationPseudoLenTests extends BaseValidationTests {

	@Test
	public void test1() {
	    chkStringRule("len == 0", "abc", false);
	    chkStringRule("len == 3", "abc", true);
	    chkStringRule("len > 0", "abc", true);
	}
	
	//do list later!!
	
	private void chkStringRule(String text, String str, boolean ok) {
		String s = String.format("type Foo string %s end let x Foo = '%s'", text, str);
		parseAndValidate(s, ok, "STRING_SHAPE");
	}

}
