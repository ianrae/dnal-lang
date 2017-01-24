package com.github.ianrae.dnalparse.compiler;

import static org.junit.Assert.assertEquals;

import org.dnal.core.DValue;
import org.dnal.core.ValidationState;
import org.junit.Test;

public class ValidationStructTests extends BaseValidationTests {

	@Test
	public void test1() {
	    chkRule("", 99, true);
	    chkRule("x == 99", 99, true);
		chkRule("x == 99", 100, false);
	}
	
    @Test
    public void testLen() {
        chkStringRule("len(x) == 99", "abc", false);
        chkStringRule("len(x) == 3", "abc", true);
    }

    
    @Test
    public void testEmpty() {
        chkStringRule("empty(x)", "abc", false);
        chkStringRule("empty(x)", "", true);
        chkStringRule("!empty(x)", "", false);
    }
    
    
	private void chkRule(String op, int n, boolean ok) {
		String s = String.format("type Foo struct { x int } %s end let x Foo = { %d }", op, n);
		parseAndValidate(s, ok);
	}
    private void chkStringRule(String op, String str, boolean ok) {
        String s = String.format("type Foo struct { x string } %s end let x Foo = { '%s' }", op, str);
        parseAndValidate(s, ok);
    }
	private void parseAndValidate(String input, boolean expected) {
		parseAndValidate(input, expected, null);
	}

	protected void chkInvalid(DValue dval) {
		assertEquals(ValidationState.INVALID, dval.getValState());
	}

}
