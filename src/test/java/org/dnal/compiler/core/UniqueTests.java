package org.dnal.compiler.core;

import static org.junit.Assert.assertEquals;

import org.dnal.core.DValue;
import org.dnal.core.ValidationState;
import org.junit.Test;

public class UniqueTests extends BaseValidationTests {

	@Test
	public void test1() {
	    chkRule("", 99, 100, 101, true);
	    DValue dval = getContext().world.findTopLevelValue("x");
	    assertEquals(3, dval.asList().size());
	}
	@Test
	public void test2() {
	    chkRule("", 99, 100, 99, false);
	}
	
	private void chkRule(String op, int n1, int n2, int n3, boolean ok) {
		String s = String.format("type Foo struct { x int unique} %s end let x list<Foo> = [{ %d }, {%d}, {%d} ]", op, n1, n2, n3);
		parseAndValidate(s, ok);
	}
	private void parseAndValidate(String input, boolean expectedPass) {
		parseAndValidate(input, expectedPass, null);
	}

	protected void chkInvalid(DValue dval) {
		assertEquals(ValidationState.INVALID, dval.getValState());
	}

}
