package org.dnal.core.util;

public class StringTrail {
	private String trail;
	private String delim = ";";
	
	public StringTrail() {
	}
	public StringTrail(String[] ar) {
		for(String s : ar) {
			add(s);
		}
	}
	public void setDelim(String delim) {
		this.delim = delim;
	}
	
	public void add(String s) {
		if (trail == null) {
			trail = s;
		} else {
			trail += delim + s;
		}
	}

	@Override
	public String toString() {
		return trail;
	}
	
	
}
