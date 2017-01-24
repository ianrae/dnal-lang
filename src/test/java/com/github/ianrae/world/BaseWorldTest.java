package com.github.ianrae.world;

import java.util.Date;

import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.dnalgenerate.DateFormatParser;
import com.github.ianrae.dnalparse.impl.CompilerContext;
import com.github.ianrae.dnalparse.impl.CompilerImpl;

public class BaseWorldTest {
    
    protected CompilerImpl aCompiler;
    
    protected DNALCompiler createCompiler() {
        aCompiler = new CompilerImpl();
        aCompiler.getCompilerOptions().useMockImportLoader(true); //!!
        return aCompiler;
    }
    
    protected CompilerContext getContext() {
        return aCompiler.getContext();
    }


    protected  void log(String s) {
        System.out.println(s);
    }

    protected Date makeDate(String s) {
        return DateFormatParser.parse(s);
    }
    

}
