package org.dnal.compiler.validate;

public class ValidationOptions {
	public static final int VALIDATEMODE_VALUES = 0x01;
	public static final int VALIDATEMODE_REFS = 0x02;
	public static final int VALIDATEMODE_EXISTENCE = 0x04;
	public static final int VALIDATEMODE_ALL = 0x07;
	
	public int validationMode = VALIDATEMODE_ALL;
	public boolean revalidationEnabled = true;
	
	public boolean isModeSet(int mode) {
    	int mask = validationMode & mode;
    	return (mask != 0);
	}

	public ValidationOptions createCopy() {
		ValidationOptions copy = new ValidationOptions();
		copy.validationMode = this.validationMode;
		copy.revalidationEnabled = this.revalidationEnabled;
		return copy;
	}
}
