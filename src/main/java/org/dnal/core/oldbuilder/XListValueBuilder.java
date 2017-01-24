package org.dnal.core.oldbuilder;

import java.util.ArrayList;
import java.util.List;

import org.dnal.core.DListType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;

public class XListValueBuilder extends XDValueBuilder {
	private DListType listType;
	private DType elementType;
	private List<DValue> list = new ArrayList<>();

	public XListValueBuilder(DListType type) {
		if (!type.isShape(Shape.LIST)) {
			addWrongTypeError("expecting list");
			return;
		}
		this.type = type;
		this.listType = type;
		this.elementType = listType.getElementType();
	}

	public void buildFromString(String input) {
		//do nothing
	}
	public void addValue(DValue dval) {
	    if (dval == null) {
	        addNoDataError();
	        return;
	    } else if (! elementType.isAssignmentCompatible(dval.getType())) {
			addWrongTypeError("expecting int (tc)");
			return;
		}
		list.add(dval);
	}
	

	@Override
	protected void onFinish() {
		if (wasSuccessful()) {
			newDVal = new DValueImpl(type, list);
		}
	}
}