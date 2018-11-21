package org.dnal.core.xbuilder;

import org.dnal.core.DType;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;

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
			
			//use .valueOf to save memory. it re-uses the same instances for common values.
			nval = Integer.valueOf(nval.intValue());
			
			this.newDVal = new DValueImpl(type, nval);
		} catch (NumberFormatException e) {
			addParsingError(String.format("'%s' is not an integer", input), input);
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