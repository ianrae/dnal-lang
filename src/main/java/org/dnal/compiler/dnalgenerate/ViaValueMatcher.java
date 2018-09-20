package org.dnal.compiler.dnalgenerate;

import org.dnal.core.DValue;
import org.dnal.core.Shape;

public class ViaValueMatcher {
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