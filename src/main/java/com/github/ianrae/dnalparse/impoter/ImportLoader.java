package com.github.ianrae.dnalparse.impoter;

import com.github.ianrae.dnalparse.impl.CompilerContext;

public interface ImportLoader {

    void importPackage(String pkg, CompilerContext context);
}
