package com.github.ianrae.dnalparse;

import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.MyWorld;

import com.github.ianrae.dnalparse.impl.CompilerContext;

public interface DValueLoader<T> {
    Class<?> willLoad();
    T create(DValue dval);
    DValue createDValue(Object bean);
    void attach(DTypeRegistry registry, MyWorld world, CompilerContext context);
}