package org.dnal.core.xbuilder;

import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;

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