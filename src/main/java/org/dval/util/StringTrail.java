package org.dval.util;

public class StringTrail {
	private String trail;
	
	public StringTrail() {
	}
	public StringTrail(String[] ar) {
		for(String s : ar) {
			add(s);
		}
	}
	
	public void add(String s) {
		if (trail == null) {
			trail = s;
		} else {
			trail += ";" + s;
		}
	}

	@Override
	public String toString() {
		return trail;
	}
	
	
}
