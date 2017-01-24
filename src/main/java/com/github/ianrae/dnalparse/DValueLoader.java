package com.github.ianrae.dnalparse;

import org.dval.DTypeRegistry;
import org.dval.DValue;
import org.dval.repository.MyWorld;

import com.github.ianrae.dnalparse.impl.CompilerContext;

public interface DValueLoader<T> {
    Class<?> willLoad();
    T create(DValue dval);
    DValue createDValue(Object bean);
    void attach(DTypeRegistry registry, MyWorld world, CompilerContext context);
}