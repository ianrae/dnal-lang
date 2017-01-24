package org.dval.oldbuilder;

import java.util.ArrayList;
import java.util.List;

import org.dval.DListType;
import org.dval.DType;
import org.dval.DValue;
import org.dval.DValueImpl;
import org.dval.Shape;

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