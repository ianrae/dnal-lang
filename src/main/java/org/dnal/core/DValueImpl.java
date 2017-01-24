package org.dnal.core;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class DValueImpl implements DValue {
    private DType type;
    private Object object;
    private ValidationState valState = ValidationState.UNKNOWN;
    private Object persistenceId;

    public DValueImpl(DType type, Object object) {
        super();
        this.type = type;
        this.object = object;
    }

    @Override
    public DType getType() {
        return type;
    }
    @Override
    public Object getObject() {
        return object;
    }
    @Override
    public ValidationState getValState() {
        return valState;
    }
    @Override
    public boolean isValid() {
        return valState == ValidationState.VALID;
    }

    public void changeValidState(ValidationState valState) {
        this.valState = valState;
    }
    
    public void forceObject(Object obj) {
        this.object = obj;
    }

    @Override
    public int asInt() {
        if (object instanceof Integer) {
            Integer lval = (Integer) object;
            return lval.intValue();
        } else {
            Long lval = (Long) object;
            return lval.intValue();
        }
    }
    @Override
    public double asNumber() {
        Double lval = (Double) object;
        return lval.doubleValue();
    }
    @Override
    public long asLong() {
        if (object instanceof Integer) {
            Integer lval = (Integer) object;
            return lval.longValue();
        } else {
            Long lval = (Long) object;
            return lval.longValue();
        }
    }
    @Override
    public String asString() {
        return object.toString();
    }
    @Override
    public boolean asBoolean() {
        Boolean bool = (Boolean) object;
        return bool;
    }
    @Override
    public Date asDate() {
        Date dt = (Date) object;
        return dt;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DValue> asList() {
        return (List<DValue>) object;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String,DValue> asMap() {
        return (Map<String, DValue>) object;
    }
    
    @Override
    public DStructHelper asStruct() {
        return new DStructHelper(this);
    }

    @Override
    public Object getPersistenceId() {
        return persistenceId;
    }

    public void setPersistenceId(Object persistenceId) {
        this.persistenceId = persistenceId;
    }
}