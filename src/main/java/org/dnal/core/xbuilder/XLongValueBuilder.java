package org.dnal.core.xbuilder;

import org.dnal.core.DType;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;

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
            
			//use .valueOf to save memory. it re-uses the same instances for common values.
			nval = Long.valueOf(nval.longValue());
            
            this.newDVal = new DValueImpl(type, nval);
        } catch (NumberFormatException e) {
            addParsingError(String.format("'%s' is not a long", input), input);
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