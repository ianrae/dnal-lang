package org.dnal.compiler.impoter;

import org.dnal.api.impl.CompilerContext;
import org.dnal.core.logger.Log;

public class MockImportLoader implements ImportLoader {

    @Override
    public void importPackage(String pkg, CompilerContext context) {
        Log.log("mock import: " + pkg);
    }

}
