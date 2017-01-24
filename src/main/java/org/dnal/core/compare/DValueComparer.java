package org.dnal.core.compare;

import java.util.List;
import java.util.Map;

import org.dnal.core.DValue;
import org.dnal.core.Shape;

public class DValueComparer {

	/**
	 * for now the types must be the same. can't compare a base class and derived class.
	 * @param dval1
	 * @param dval2
	 * @return
	 */
	public boolean compare(DValue dval1, DValue dval2) {
		if (dval1 == null || dval2 == null) {
			return false;
		} else if (dval1 == dval2) {
			return true;
		} else if (dval1.getType() != dval2.getType()) {
			return false;
		}

		if (dval1.getType().isScalarShape()) {
			Object obj1 = dval1.getObject();
			Object obj2 = dval2.getObject();
			if (obj1 == null || obj2 == null) {
				return obj1 == obj2; //false unless both are null
			}

			return obj1.equals(obj2);
		} else if (dval1.getType().isShape(Shape.LIST)) {
			return compareLists(dval1, dval2);
		} else if (dval1.getType().isShape(Shape.STRUCT)) {
			return compareStructs(dval1, dval2);
		}

		return false;
	}

	private boolean compareStructs(DValue dval1, DValue dval2) {
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
			
			if (! this.compare(inner1, inner2)) {
				return false;
			}
		}
		return true;
	}

	private boolean compareLists(DValue dval1, DValue dval2) {
		List<DValue> list1 = dval1.asList();
		List<DValue> list2 = dval2.asList();
		
		if (list1.size() != list2.size()) {
			return false;
		}
		
		for(int i = 0; i < list1.size(); i++) {
			if (! this.compare(list1.get(i), list2.get(i))) {
				return false;
			}
		}
		return true;
	}
}