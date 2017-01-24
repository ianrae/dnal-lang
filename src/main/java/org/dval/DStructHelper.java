package org.dval;

import java.util.Map;
import java.util.Set;

//convenience class only
public class DStructHelper {
	private DValue dval;
	
	public DStructHelper(DValue dval) {
		this.dval = dval;
	}

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
