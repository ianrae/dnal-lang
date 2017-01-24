package org.dnal.compiler.impoter;

import org.dnal.api.impl.CompilerContext;

public interface ImportLoader {

    void importPackage(String pkg, CompilerContext context);
}
