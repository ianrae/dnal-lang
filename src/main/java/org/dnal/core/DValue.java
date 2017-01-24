package org.dnal.core;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface DValue {
    
	DType getType();
	Object getObject();
	ValidationState getValState();
	boolean isValid();
//	void changeValidState(ValidationState valState);
//	void forceObject(Object obj);
	int asInt();
    double asNumber();
	long asLong();
	String asString();
	boolean asBoolean();
	Date asDate();
	List<DValue> asList();
	Map<String,DValue> asMap();
	DStructHelper asStruct();
    Object getPersistenceId();
//    void setPersistenceId(Object persistenceId);
}