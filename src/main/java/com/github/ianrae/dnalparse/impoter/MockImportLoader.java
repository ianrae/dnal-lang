package com.github.ianrae.dnalparse.impoter;

import org.dval.logger.Log;

import com.github.ianrae.dnalparse.impl.CompilerContext;

public class MockImportLoader implements ImportLoader {

    @Override
    public void importPackage(String pkg, CompilerContext context) {
        Log.log("mock import: " + pkg);
    }

}
