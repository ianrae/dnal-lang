package org.dnal.core.xbuilder;

import org.dnal.core.DType;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;

public class XBooleanValueBuilder extends XDValueBuilder {
	public XBooleanValueBuilder(DType type) {
		if (!type.isShape(Shape.BOOLEAN)) {
			addWrongTypeError("expection boolean");
			return;
		}
		this.type = type;
	}

	public void buildFromString(String input) {
		if (input == null) {
			addNoDataError("no data");
			return;
		}

		Boolean bool = null;
		try {
			String target = "true";
			String target2 = "false";
			if (target.equalsIgnoreCase(input) || target2.equalsIgnoreCase(input)) {
				bool = Boolean.parseBoolean(input);
				this.newDVal = new DValueImpl(type, bool);
			} else {
				addParsingError(String.format("'%s' is not an boolean", input), input);
			}
		} catch (NumberFormatException e) {
			addParsingError(String.format("'%s' is not an boolean", input), input);
		}
	}
	public void buildFrom(Boolean bool) {
		if (bool == null) {
			addNoDataError("no data");
			return;
		}
		this.newDVal = new DValueImpl(type, bool);
	}

	@Override
	protected void onFinish() {
	}
}