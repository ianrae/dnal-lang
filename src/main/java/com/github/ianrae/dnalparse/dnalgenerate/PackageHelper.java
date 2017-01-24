package com.github.ianrae.dnalparse.dnalgenerate;

import org.dval.DType;
import org.dval.DTypeRegistry;
import org.dval.util.NameUtils;

public class PackageHelper  {
    protected DTypeRegistry registry;
    private String packageName;

    public PackageHelper(DTypeRegistry regsitry, String packageName) {
        this.packageName = packageName;
        if (NameUtils.isNullOrEmpty(packageName)) {
            this.packageName = null;
        }
        this.registry = regsitry;
    }

    public void registerType(String typeName, DType dtype) {
        addPackage(dtype);
        String completeName = dtype.getCompleteName();
        registry.add(completeName, dtype);
    }
    public void addPackage(DType dtype) {
        if (packageName != null) {
            dtype.setPackageName(packageName);
        }
    }
    public String buildCompleteName(String name) {
        return NameUtils.completeName(packageName, name);
    }
    public DType findRegisteredType(String typeName) {
        String completeName = buildCompleteName(typeName);
        DType dtype = registry.getType(completeName);
        if (dtype != null) {
            return dtype;
        }
        return registry.getType(typeName);
    }
    public boolean existsRegisteredType(String typeName) {
        return (findRegisteredType(typeName) != null);
    }

    public String getPackageName() {
        return packageName;
    }


}