package org.dval.oldbuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dval.DType;
import org.dval.DValue;
import org.dval.DValueImpl;
import org.dval.Shape;

public class XDateValueBuilder extends XDValueBuilder {
	private String dateFormat = "dd-MMM-yy";
	
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
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat) ; 
			dt = sdf.parse(input);
			this.newDVal = new DValueImpl(type, dt);
		} catch (ParseException e) {
			addParsingError(String.format("'%s' is not a date", input));
		}
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

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
}