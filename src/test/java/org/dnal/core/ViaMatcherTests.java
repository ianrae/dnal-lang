package org.dnal.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class ViaMatcherTests extends BaseDValTest {
	public static class ViaValueMatcher {
		private String errMsg;
		
		public boolean match(String value, DValue dval) {
			
			boolean b = false;
			
			try {
				b = doMatch(value, dval);
			} catch (NumberFormatException e) {
				this.errMsg = String.format("NumberFormatException: %s", e.getMessage());
			} catch (Exception e) {
				this.errMsg = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
			}
			
			return b;
		}
		
		private boolean doMatch(String value, DValue dval) {
			if (dval.getType().isShape(Shape.INTEGER)) {
				int target = dval.asInt();
				Integer other = Integer.parseInt(value); //handle parsing errors!!
				return (other.intValue() == target);
			} else if (dval.getType().isShape(Shape.LONG)) {
				long target = dval.asLong();
				Long other = Long.parseLong(value); //handle parsing errors!!
				return (other.longValue() == target);
			} else {
				//will handle BOOLEAN, ENUM
				//should not do via on NUMBER because floats are imprecise by nature
				String s1 = value;
				String s2 = dval.asString();
				return (s1.equals(s2));
			}
		}

		public String getErrMsg() {
			return errMsg;
		}
	}

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
