package org.dnal.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//convenience class only
public class DStructHelper {
	private DValue dval;
	
	public DStructHelper(DValue dval) {
		this.dval = dval;
	}

	//names are not ordered by declaration order (use DStructType.orderedList for that)
	public Set<String> getFieldNames() {
		Map<String, DValue> map = dval.asMap();
		return map.keySet();
	}
	
	public DValue getField(String fieldName) {
		Map<String, DValue> map = dval.asMap();
		return map.get(fieldName);
	}
    public boolean isFieldOptional(String fieldName) {
       DStructType dtype = (DStructType) dval.getType();
       return dtype.fieldIsOptional(fieldName);
    }

    public DValue getDval() {
        return dval;
    }
}
