package org.dnal.api;

import org.dnal.api.impl.CompilerContext;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.World;

public interface DValueLoader<T> {
    Class<?> willLoad();
    T create(DValue dval);
    DValue createDValue(Object bean);
    void attach(DTypeRegistry registry, World world, CompilerContext context);
}