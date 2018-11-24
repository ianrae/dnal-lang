package org.dnal.core;

public interface DValueInternal {

    void changeValidState(ValidationState valState);
    void setPersistenceId(Object persistenceId);
}