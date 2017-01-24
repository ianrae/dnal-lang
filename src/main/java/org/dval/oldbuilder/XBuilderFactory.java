package org.dval.oldbuilder;

import org.dval.DListType;
import org.dval.DStructType;
import org.dval.DType;

public class XBuilderFactory {

	public XDValueBuilder createBuilderFor(DType type) {
		switch(type.getShape()) {
		case INTEGER:
			return new XIntegerValueBuilder(type);
        case NUMBER:
            return new XNumberValueBuilder(type);
		case STRING:
			return new XStringValueBuilder(type);
		case BOOLEAN:
			return new XBooleanValueBuilder(type);
		case DATE:
			return new XDateValueBuilder(type);
		case LIST:
			return new XListValueBuilder((DListType)type);
		case STRUCT:
			return new XStructValueBuilder((DStructType)type);
		default:
			return null;
		}
	}
}
