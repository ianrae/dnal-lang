package org.dnal.compiler.validate;

import java.util.HashMap;
import java.util.Map;

public class ValidationOptions {
	//NONE is an advanced mode. use carefully
	public static final int VALIDATEMODE_NONE = 0x00;
	public static final int VALIDATEMODE_VALUES = 0x01;
	public static final int VALIDATEMODE_REFS = 0x02;
	public static final int VALIDATEMODE_EXISTENCE = 0x04;
	public static final int VALIDATEMODE_ALL = 0x07;
	
	public int validationMode = VALIDATEMODE_ALL;
	public boolean revalidationEnabled = true;
	
	//validation rule objects are created once at dnal compile time
	//In order to support dynamic rule behaviour (that is, changing
	//behaviour within a dataset and within a transaction)
	//we need a place to pass data to custom rules
	public Map<String,Object> passThroughMap = new HashMap<>();
	
	public boolean isModeSet(int mode) {
    	int mask = validationMode & mode;
    	return (mask != 0);
	}

	public ValidationOptions createCopy() {
		ValidationOptions copy = new ValidationOptions();
		copy.validationMode = this.validationMode;
		copy.revalidationEnabled = this.revalidationEnabled;
		copy.passThroughMap = new HashMap<>(passThroughMap);
		return copy;
	}
}
