package com.github.ianrae.dnalparse;

import org.dnal.api.impl.CompilerContext;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.MyWorld;

public interface DValueLoader<T> {
    Class<?> willLoad();
    T create(DValue dval);
    DValue createDValue(Object bean);
    void attach(DTypeRegistry registry, MyWorld world, CompilerContext context);
}