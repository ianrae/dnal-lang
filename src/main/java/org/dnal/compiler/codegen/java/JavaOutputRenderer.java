package org.dnal.compiler.codegen.java;

import com.github.ianrae.dnalc.ConfigFileOptions;

public class JavaOutputRenderer {
    private ConfigFileOptions options;

    public JavaOutputRenderer(ConfigFileOptions options) {
        this.options = options;
    }
    
    public String buildOutputPath(String javaFilename) {
        String path = options.outputPath;
        if (! path.endsWith("/")) {
            path += "/";
        }
        String s = options.javaPackage.replace('.', '/');
        path += s;
        if (! path.endsWith("/")) {
            path += "/";
        }
        
        path = String.format("%s%s.java", path, javaFilename);
        return path;
    }
}