package org.dnal.core.nrule;

import org.dnal.core.DValue;

public class ValidationScorer {
	private int validCount;
	private int invalidCount;
	private int unknownCount;
	public int count;
	
	public void score(DValue dval) {
		count++;
		switch(dval.getValState()) {
		case VALID:
			validCount++;
			break;
		case INVALID:
			invalidCount++;
			break;
		case UNKNOWN:
			unknownCount++;
			break;
		default:
			break;
		}
	}
	
	public boolean allValid() {
		return validCount == count;
	}
	public boolean someInvalid() {
		return invalidCount > 0;
	}
	public boolean someUnknown() {
		return unknownCount > 0;
	}

	public int getValidCount() {
		return validCount;
	}

	public int getInvalidCount() {
		return invalidCount;
	}

	public int getUnknownCount() {
		return unknownCount;
	}

	public int getCount() {
		return count;
	}
}
