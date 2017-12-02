package org.dnal.core.xbuilder;

import java.util.Map;
import java.util.TreeMap;

import org.dnal.core.DMapType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;

public class XMapValueBuilder extends XDValueBuilder {
	private DMapType mapType;
    public Map<String,DValue> map = new TreeMap<>();

	public XMapValueBuilder(DMapType type) {
		if (!type.isShape(Shape.MAP)) {
			addWrongTypeError("expecting map");
			return;
		}
		this.type = type;
		this.mapType = type;
//		this.allFields = type.getAllFields();
	}

	public void buildFromString(String input) {
		//do nothing
	}
	public void addElement(String fieldName, DValue dval) {
		addElement(fieldName, dval, true);
	}
	public void addElement(String fieldName, DValue dval, boolean logNullErr) {
		if (fieldName == null || fieldName.isEmpty()) {
			addNoDataError("null or empty fieldname");
			return;
		}
		
		DType target = mapType.getElementType();
		if (! target.isAssignmentCompatible(dval.getType())) {
			this.addWrongTypeError(String.format("field %s", fieldName)); //!!
		}
		
		map.put(fieldName, dval);
	}


	@Override
	protected void onFinish() {
		if (wasSuccessful()) {
			newDVal = new DValueImpl(type, map);
		}
	}
	
	
}