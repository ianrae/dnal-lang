package org.dnal.compiler.core;

import static org.junit.Assert.assertEquals;

import org.dnal.compiler.validate.ValidationOptions;
import org.dnal.core.DValue;
import org.dnal.core.ValidationState;
import org.junit.Test;

public class ValidationModeTests extends BaseValidationTests {

	@Test
	public void testAll() {
		String dnal = buildDNAL(99, 100, 101);
		validationMode = ValidationOptions.VALIDATEMODE_ALL;
	    chkRule(dnal, true);
	    DValue dval = getContext().world.findTopLevelValue("x");
	    assertEquals(3, dval.asList().size());
	}
	@Test
	public void testValues() {
		String dnal = buildDNAL(99, 100, 99);
		validationMode = ValidationOptions.VALIDATEMODE_VALUES;
	    chkRule(dnal, true);
//	    
//		dnal = buildDNAL(99, 100, 99);
//		validationMode = ValidationOptions.VALIDATEMODE_ALL;
//	    chkRule(dnal, false);
	}
	@Test
	public void testExistence() {
        String dnal =  "type Foo struct { x string optional, y string } end let x Foo = { 'abc', null }";
		validationMode = ValidationOptions.VALIDATEMODE_EXISTENCE;
	    chkRule(dnal, false);
	    
	    log("AGAIN...");
		validationMode = ValidationOptions.VALIDATEMODE_VALUES;
	    chkRule(dnal, true);
	}
	
	
	//---
	private int validationMode = ValidationOptions.VALIDATEMODE_ALL;
	
	private String buildDNAL(int n1, int n2, int n3) {
		String dnal = String.format("type Foo struct { x int unique} end let x list<Foo> = [{ %d }, {%d}, {%d} ]", n1, n2, n3);
		return dnal;
	}
	
	private void chkRule(String dnal, boolean ok) {
		parseAndValidate(dnal, ok);
	}
	private void parseAndValidate(String input, boolean expectedPass) {
		parseAndValidate(input, expectedPass, null, validationMode);
	}

	protected void chkInvalid(DValue dval) {
		assertEquals(ValidationState.INVALID, dval.getValState());
	}

}
