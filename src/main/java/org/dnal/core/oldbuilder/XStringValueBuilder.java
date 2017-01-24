package org.dnal.core.oldbuilder;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;

public class XStringValueBuilder extends XDValueBuilder {

	public XStringValueBuilder(DType type) {
		if (!type.isShape(Shape.STRING)) {
			addWrongTypeError("expecting string");
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