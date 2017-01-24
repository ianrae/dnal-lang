package org.dval.oldbuilder;

import org.dval.DType;
import org.dval.DValue;
import org.dval.DValueImpl;
import org.dval.Shape;

public class XNumberValueBuilder extends XDValueBuilder {
    public XNumberValueBuilder(DType type) {
        if (!type.isShape(Shape.NUMBER)) {
            addWrongTypeError("expecting number");
            return;
        }
        this.type = type;
    }

    public void buildFromString(String input) {
        if (input == null) {
            addNoDataError("no data");
            return;
        }

        Double nval = null;
        try {
            nval = Double.parseDouble(input);
            this.newDVal = new DValueImpl(type, nval);
        } catch (NumberFormatException e) {
            addParsingError(String.format("'%s' is not an number", input));
        }
    }
    public void buildFrom(Double lval) {
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