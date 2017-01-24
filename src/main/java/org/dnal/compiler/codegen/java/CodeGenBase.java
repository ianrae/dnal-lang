package org.dnal.compiler.codegen.java;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.parser.ast.FullTypeExp;
import org.dnal.compiler.parser.ast.StructMemberExp;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.logger.Log;
import org.dnal.core.util.TextFileWriter;

import com.github.ianrae.dnalc.ConfigFileOptions;

public abstract class CodeGenBase extends TypeOnlyGenerator {
    public List<String> outputL = new ArrayList<>();
    protected ConfigFileOptions options;
    protected String currentName;
    protected JavaST st = new JavaST();
    protected DType currentType;
    protected ImportObserver importObserver;

    public CodeGenBase(ConfigFileOptions options) {
        this.options = options;
    }
    
    protected boolean writeFile(String javaFilename) {
        JavaOutputRenderer r = new JavaOutputRenderer(options);
        String path = r.buildOutputPath(javaFilename);
        Log.log("writing " + path);
        TextFileWriter w = new TextFileWriter();
        return w.writeFile(path, outputL);
    }
    
    protected void replaceImport() {
        List<String> L = new ArrayList<>();
        for(String s: outputL) {
            if (s.contains("***imports***")) {
                String[] ar = s.split("\n");
                for(int i = 0; i < ar.length; i++) {
                    String ss = ar[i].trim();
                    if (ss.contains("***imports***")) {
                        for(String imp: importObserver.importL) {
                            L.add(String.format("import %s;", imp));
                        }
                    } else {
                        L.add(ss);
                    }
                }
            } else {
                L.add(s.trim());
            }
        }
        
        outputL = L;
    }
    
    protected void onStartType(String name, DType dtype) {
        currentName = name;
        currentType = dtype;
        importObserver = new ImportObserver();
        importObserver.addImport(dtype);
    }
    protected void onEndType(String name, String suffix) {
        outputL.add("}");
        replaceImport();
        this.writeFile(currentName + suffix);
    }
    protected DType onStartMember(String fieldName) {
        DStructType xx = (DStructType) currentType;
        DType membType = xx.getFields().get(fieldName);
        importObserver.addImport(membType);
        return membType;
    }
    
    
}