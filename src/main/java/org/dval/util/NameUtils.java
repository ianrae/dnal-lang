package org.dval.util;

public class NameUtils {

    public static String completeName(String packageName, String name) {
        if (isNullOrEmpty(packageName)) {
            return name;
        } else {
            return packageName + "." + name;
        }
    }
    
    public static boolean isNullOrEmpty(String s) {
        if (s == null || s.isEmpty()) {
            return true; 
        } else {
            return false;
        }
    }
}
