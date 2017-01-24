package org.dval.oldbuilder;

import org.dval.DType;
import org.dval.DValue;
import org.dval.DValueImpl;
import org.dval.Shape;

public class XLongValueBuilder extends XDValueBuilder {
    public XLongValueBuilder(DType type) {
        if (!type.isShape(Shape.LONG)) {
            addWrongTypeError("expecting long");
            return;
        }
        this.type = type;
    }

    public void buildFromString(String input) {
        if (input == null) {
            addNoDataError("no data");
            return;
        }

        Long nval = null;
        try {
            nval = Long.parseLong(input);
            this.newDVal = new DValueImpl(type, nval);
        } catch (NumberFormatException e) {
            addParsingError(String.format("'%s' is not an integer", input));
        }
    }
    public void buildFrom(Long lval) {
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