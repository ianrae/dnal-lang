package org.dnal.api;

public class CompilerOptions {
    private boolean useMockImportLoader = false;
    private boolean useProxyDValues = true;
    
    public void useMockImportLoader(boolean b) {
        this.useMockImportLoader = b;
    }

    public void useProxyDValues(boolean useProxyDValues) {
        this.useProxyDValues = useProxyDValues;
    }

    public boolean isUseMockImportLoader() {
        return useMockImportLoader;
    }

    public boolean isUseProxyDValues() {
        return useProxyDValues;
    }

}
