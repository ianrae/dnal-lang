package org.dnal.core;

import static org.junit.Assert.*;

import org.dnal.compiler.dnalgenerate.ViaValueMatcher;
import org.junit.Test;

public class ViaMatcherTests extends BaseDValTest {
	@Test
	public void testInt() {
		DValue dval = createInt(55);
		ViaValueMatcher matcher = new ViaValueMatcher();
		assertEquals(false, matcher.match("34", dval));
		assertEquals(true, matcher.match("55", dval));
		assertEquals(true, matcher.match("055", dval)); //leading zeroes
		
		dval = createInt(-55);
		assertEquals(false, matcher.match("34", dval));
		assertEquals(true, matcher.match("-55", dval));
	}
	@Test
	public void testLong() {
		DValue dval = createLong(1400L);

		ViaValueMatcher matcher = new ViaValueMatcher();
		assertEquals(false, matcher.match("34", dval));
		assertEquals(true, matcher.match("1400", dval));
		assertEquals(true, matcher.match("01400", dval)); //leading zeroes
	}
	
	@Test
	public void testErr() {
		DValue dval = createLong(1400L);
		ViaValueMatcher matcher = new ViaValueMatcher();
		assertEquals(false, matcher.match("zzz", dval)); //leading zeroes
		assertEquals("NumberFormatException: For input string: \"zzz\"", matcher.getErrMsg());
	}
	
	//--
	DValue createInt(int n) {
		DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
		Integer nval = n;
		DValue dval = new DValueImpl(type, nval);
		return dval;
	}
	DValue createLong(long n) {
		DType type = registry.getType(BuiltInTypes.LONG_SHAPE);
		Long nval = n;
		DValue dval = new DValueImpl(type, nval);
		return dval;
	}
	
}
