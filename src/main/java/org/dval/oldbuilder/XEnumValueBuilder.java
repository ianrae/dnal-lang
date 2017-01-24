package org.dval.oldbuilder;

import org.dval.DType;
import org.dval.DValue;
import org.dval.DValueImpl;
import org.dval.Shape;

public class XEnumValueBuilder extends XDValueBuilder {

	public XEnumValueBuilder(DType type) {
		if (!type.isShape(Shape.ENUM)) {
			addWrongTypeError("expecting enum");
			return;
		}
		this.type = type;
	}

	public void buildFromString(String input) {
		if (input == null) {
			addNoDataError("no data");
			return;
		}

		this.newDVal = new DValueImpl(type, input);
	}
	

	@Override
	protected void onFinish() {
	}
}