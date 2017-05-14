package org.dnal.api;

public class CompilerOptions {
    private boolean useMockImportLoader = false;
    
    //dval works with or without proxy values. They wrap a dval so that
    //you can change a value withouts it 'pointer' changing.
    //Is not needed if you create but never modify your dvals.
    //Is needed to implement an 'update' of the data corral.
    private boolean useProxyDValues = false; //true;
    
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
