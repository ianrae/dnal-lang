package org.dnal.core.xbuilder;

import java.util.Date;

import org.dnal.compiler.dnalgenerate.DateFormatParser;
import org.dnal.core.DType;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;

public class XDateValueBuilder extends XDValueBuilder {
	private DateFormatParser dateParser = new DateFormatParser();

	public XDateValueBuilder(DType type) {
		if (!type.isShape(Shape.DATE)) {
			addWrongTypeError("expecting date");
			return;
		}
		this.type = type;
	}

	public void buildFromString(String input) {
		if (input == null) {
			addNoDataError("no data");
			return;
		}

		Date dt = null;
		dt = dateParser.parse(input);
		if (dt == null) {
			this.addParsingError(String.format("Can't convert '%s' to date", input), input);
			return;
		}
		this.newDVal = new DValueImpl(type, dt);
	}
	public void buildFrom(Date dt) {
		if (dt == null) {
			addNoDataError("no data");
			return;
		}
		this.newDVal = new DValueImpl(type, dt);
	}

	@Override
	protected void onFinish() {
	}

}