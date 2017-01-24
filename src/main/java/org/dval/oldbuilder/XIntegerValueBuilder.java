package org.dval.oldbuilder;

import org.dval.DType;
import org.dval.DValue;
import org.dval.DValueImpl;
import org.dval.Shape;

public class XIntegerValueBuilder extends XDValueBuilder {
	public XIntegerValueBuilder(DType type) {
		if (!type.isShape(Shape.INTEGER)) {
			addWrongTypeError("expecting int");
			return;
		}
		this.type = type;
	}

	public void buildFromString(String input) {
		if (input == null) {
			addNoDataError("no data");
			return;
		}

		Integer nval = null;
		try {
			nval = Integer.parseInt(input);
			this.newDVal = new DValueImpl(type, nval);
		} catch (NumberFormatException e) {
			addParsingError(String.format("'%s' is not an integer", input));
		}
	}
	public void buildFrom(Integer lval) {
		if (lval == null) {
			addNoDataError("no data");
			return;
		}
		this.newDVal = new DValueImpl(type, lval);
	}

	@Override
	protected void onFinish() {
	}
}