package com.github.ianrae.dnalparse.systest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.logger.Log;
import org.dnal.core.repository.MyWorld;

import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.DataSet;
import com.github.ianrae.dnalparse.dnalgenerate.DateFormatParser;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.impl.CompilerImpl;
import com.github.ianrae.dnalparse.impl.DataSetImpl;
import com.github.ianrae.dnalparse.impoter.ImportLoader;
import com.github.ianrae.dnalparse.impoter.MockImportLoader;
import com.github.ianrae.dnalparse.parser.error.ErrorTrackingBase;

public class SysTestBase {
    protected boolean useMockImportLoader = false;
    protected DNALCompiler aCompiler;
    
    protected DNALCompiler createCompiler() {
        DNALCompiler compiler = new CompilerImpl();
        compiler.getCompilerOptions().useMockImportLoader(useMockImportLoader);
        aCompiler = compiler;
        return compiler;
    }

    protected DType chkType(String typeName, String source, int expectedTypes, int expectedVals) {
        chk(source, expectedTypes, expectedVals);
        return findType(typeName);
    }    
    protected DValue chkValue(String varName, String source, int expectedTypes, int expectedVals) {
        chk(source, expectedTypes, expectedVals);
        return findValue(varName);
    }    
    protected void chk(String source, int expectedTypes, int expectedVals) {
        load(source, true);
        chkTypes(expectedTypes, expectedVals);
    }    
    protected void chkFail(String source, int expectedErrors, String errMsg) {
        load(source, false);
        assertEquals(expectedErrors, errors.size());
        boolean found = false;
        for(ErrorMessage err: errors) {
            if (err.getMessage().contains(errMsg)) {
                found = true;
            }
        }
        assertEquals(true, found);
    }    
    
    protected void chkTypes(int expectedTypes, int expectedVals) {
        DataSetImpl dsi = (DataSetImpl) dataSetLoaded;
        DTypeRegistry registry = dsi.getInternals().getRegistry();
        int n = registry.getAll().size() - NUM_INTERNAL_TYPES;
        assertEquals(expectedTypes, n);
        assertEquals(expectedVals, dataSetLoaded.size());
    }
    protected DType findType(String typeName) {
        DType dtype = registry.getType(typeName);
        return dtype;
    }
    protected DValue findValue(String varName) {
        return world.findTopLevelValue(varName);
    }
    

    //-------------
    protected DataSet dataSetLoaded;
    protected List<ErrorMessage> errors;
    protected DTypeRegistry registry;
    protected MyWorld world;
    protected  static final int NUM_INTERNAL_TYPES = 7;
    
    protected  DataSet load(String source, boolean pass) {
        return load(source, pass, null);
    }
    protected  DataSet load(String source, boolean pass, DNALCompiler compilerParam) {
        XErrorTracker.logErrors = true;
        Log.debugLogging = true;

        DNALCompiler compiler = (compilerParam == null) ? createCompiler(): compilerParam;
        dataSetLoaded = compiler.compileString(source);
        DataSetImpl dsi = (DataSetImpl) dataSetLoaded;
        registry = (dataSetLoaded != null) ? dsi.getInternals().getRegistry(): null;
        world = (dataSetLoaded != null) ? dsi.getInternals().getWorld(): null;
        errors = compiler.getErrors();
        for(ErrorMessage err: compiler.getErrors()) {
            log(String.format("[%s] %d: %s", err.getSrcFile(), err.getLineNum(), err.getMessage()));
        }
        assertEquals(pass, (dataSetLoaded != null));
        return dataSetLoaded;
    }
    
    protected void chkBaseType(DType dtype, String typeName, String baseTypeName) {
        assertEquals(typeName, dtype.getBaseType().getName());
        if (baseTypeName != null) {
            assertTrue(dtype.getShape().equals(dtype.getBaseType().getShape()));
        }
    }

    protected  void log(String s) {
        System.out.println(s);
    }

    protected Date makeDate(String s) {
        return DateFormatParser.parse(s);
    }
    

}
