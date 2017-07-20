package org.dnal.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DValueProxy extends DValueImpl {
	//used when we need a placeholder for a value that
	//doesn't yet exist.
	private boolean isFutureValue;

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
    
    @Override
    public void changeValidState(ValidationState valState) {
        DValueImpl inner = (DValueImpl) super.getObject();
        inner.changeValidState(valState);
    }

	public boolean isFutureValue() {
		return isFutureValue;
	}

	public void setFutureValue(boolean isFutureValue) {
		this.isFutureValue = isFutureValue;
	}

}