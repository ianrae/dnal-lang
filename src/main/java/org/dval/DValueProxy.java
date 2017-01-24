package org.dval;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DValueProxy extends DValueImpl {

    public DValueProxy(DValue dval) {
        super(dval.getType(), dval);
    }

    @Override
    public DType getType() {
        DValue inner = (DValue) super.getObject();
        return inner.getType();
    }

    @Override
    public Object getObject() {
        DValue inner = (DValue) super.getObject();
        return inner.getObject();
    }

    @Override
    public ValidationState getValState() {
        DValue inner = (DValue) super.getObject();
        return inner.getValState();
    }

    @Override
    public boolean isValid() {
        DValue inner = (DValue) super.getObject();
        return inner.isValid();
    }

    @Override
    public int asInt() {
        DValue inner = (DValue) super.getObject();
        return inner.asInt();
    }

    @Override
    public double asNumber() {
        DValue inner = (DValue) super.getObject();
        return inner.asNumber();
    }

    @Override
    public long asLong() {
        DValue inner = (DValue) super.getObject();
        return inner.asLong();
    }

    @Override
    public String asString() {
        DValue inner = (DValue) super.getObject();
        return inner.asString();
    }

    @Override
    public boolean asBoolean() {
        DValue inner = (DValue) super.getObject();
        return inner.asBoolean();
    }

    @Override
    public Date asDate() {
        DValue inner = (DValue) super.getObject();
        return inner.asDate();
    }

    @Override
    public List<DValue> asList() {
        DValue inner = (DValue) super.getObject();
        return inner.asList();
    }

    @Override
    public Map<String, DValue> asMap() {
        DValue inner = (DValue) super.getObject();
        return inner.asMap();
    }

    @Override
    public DStructHelper asStruct() {
        DValue inner = (DValue) super.getObject();
        return inner.asStruct();
    }

    @Override
    public Object getPersistenceId() {
        DValue inner = (DValue) super.getObject();
        return inner.getPersistenceId();
    }

}