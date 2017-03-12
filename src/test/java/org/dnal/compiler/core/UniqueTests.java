package org.dnal.compiler.core;

import static org.junit.Assert.assertEquals;

import org.dnal.core.DValue;
import org.dnal.core.ValidationState;
import org.junit.Test;

public class UniqueTests extends BaseValidationTests {

	@Test
	public void test1() {
		String dnal = buildDNAL(99, 100, 101);
	    chkRule(dnal, true);
	    DValue dval = getContext().world.findTopLevelValue("x");
	    assertEquals(3, dval.asList().size());
	}
	@Test
	public void test2() {
		String dnal = buildDNAL(99, 100, 99);
	    chkRule(dnal, false);
	}
	@Test
	public void testMix() {
		String dnal = buildDNAL(99, 100, 101);
		dnal += " let y Foo = {100}";
		expected = 3;
	    chkRule(dnal, false);
	}
	
	private String buildDNAL(int n1, int n2, int n3) {
		String dnal = String.format("type Foo struct { x int unique} end let x list<Foo> = [{ %d }, {%d}, {%d} ]", n1, n2, n3);
		return dnal;
	}
	
	private void chkRule(String dnal, boolean ok) {
		parseAndValidate(dnal, ok);
	}
	private void parseAndValidate(String input, boolean expectedPass) {
		parseAndValidate(input, expectedPass, null);
	}

	protected void chkInvalid(DValue dval) {
		assertEquals(ValidationState.INVALID, dval.getValState());
	}

}
