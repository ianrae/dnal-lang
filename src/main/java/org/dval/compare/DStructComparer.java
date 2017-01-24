package org.dval.compare;

import java.util.Map;
import java.util.Set;

import org.dval.DValue;
import org.dval.Shape;

public class DStructComparer {
	private DValueComparer valueDiffer = new DValueComparer();

	/**
	 * for now the types must be the same. can't compare a base class and derived class.
	 * @param dval1
	 * @param dval2
	 * @return
	 */
	public boolean compare(DValue dval1, DValue dval2, Set<String> fieldNames) {
		if (dval1 == null || dval2 == null) {
			return dval1 == dval2;
		} else if (dval1 == dval2) {
			return true;
		} else if (dval1.getType() != dval2.getType()) {
			return false;
		}

		if (dval1.getType().isScalarShape()) {
			return false; //must be a struct
		} else if (dval1.getType().isShape(Shape.LIST)) {
			return false; //must be a struct
		} else if (dval1.getType().isShape(Shape.STRUCT)) {
			if (fieldNames == null) {
				return compareFullStructs(dval1, dval2);
			} else {
				return comparePartialStructs(dval1, dval2, fieldNames);
			}
		}

		return false;
	}

	private boolean comparePartialStructs(DValue dval1, DValue dval2,
			Set<String> fieldNames) {
		Map<String,DValue> map1 = dval1.asMap();
		Map<String,DValue> map2 = dval2.asMap();
		
		for(String fieldName : fieldNames) {
			DValue inner1 = map1.get(fieldName);
			DValue inner2 = map2.get(fieldName);
			
			if (inner1 == null || inner2 == null) {
				return inner1 == inner2;
			}
			
			//don't support nested types. fields must be scalars
			if (! valueDiffer.compare(inner1, inner2)) {
				return false;
			}
			
		}
		return true;
	}

	private boolean compareFullStructs(DValue dval1, DValue dval2) {
		Map<String,DValue> map1 = dval1.asMap();
		Map<String,DValue> map2 = dval2.asMap();
		
		if (map1.size() != map2.size()) {
			return false;
		}
		
		for(String key: map1.keySet()) {
			DValue inner1 = map1.get(key);
			DValue inner2 = map2.get(key);
			
			if (inner1 == null || inner2 == null) {
				return inner1 == inner2;
			}
			
			if (! valueDiffer.compare(inner1, inner2)) {
				return false;
			}
		}
		return true;
	}

}