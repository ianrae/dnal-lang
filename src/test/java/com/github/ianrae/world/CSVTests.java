package com.github.ianrae.world;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.impoter.MockImportLoader;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.core.DStructType;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.builder.IntBuilder;
import org.dnal.core.builder.StructBuilder;
import org.dnal.core.csv.CSVLoader;
import org.dnal.core.logger.Log;
import org.junit.Test;

import com.github.ianrae.dnalc.MySimpleVisitor;
import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.DataSet;
import com.github.ianrae.dnalparse.Generator;
import com.github.ianrae.dnalparse.Transaction;

public class CSVTests extends BaseWorldTest {

    private static final String GENERATE_DIR = "./src/main/resources/test/example/";

    @Test
    public void test() {
        DataSet dataSet = load("example-csv1.dnal");
        Transaction trans = dataSet.createTransaction();
        DStructType structType = trans.getStructType("Address");
        CSVLoader loader = new CSVLoader(GENERATE_DIR + "example1.csv", ',');
        boolean b = loader.open();
        assertEquals(true, b);
        boolean haveSeenHdr = false;
        StructBuilder builder = null;
        int count = 0;

        while(true) {
            String[] columns = loader.readLine();
            if (columns == null) {
                break;
            }

            if (! haveSeenHdr && loader.getHdr() != null) {
                haveSeenHdr = true;
            } else {
                log("line!");
                builder = trans.createStructBuilder(structType);
                int i = 0;
                builder.addField("street", columns[i++]);
                builder.addField("city", columns[i++]);
                builder.addField("flag", columns[i++]);
                builder.addField("size", columns[i++]);
                builder.addField("width", columns[i++]);

                String s = columns[i++];
                String[] ar = s.split(",");
                builder.addField("roles", ar);
                DValue dval = builder.finish();
                
                String var = String.format("val%d", count++);
                trans.add("abc", dval);
            }
        }

        b = trans.commit();
        dumpErrors(trans);
        assertEquals(true, b);
        
        log("ss");
        MySimpleVisitor visitor = new MySimpleVisitor();
        Generator gen = dataSet.createGenerator();
        b = gen.generate(visitor);
        visitor.finish();
    }

    @Test
    public void testInt() {
        DataSet dataSet = load("example-csv1.dnal");
        Transaction trans = dataSet.createTransaction();
        IntBuilder builder = trans.createIntBuilder();
        DValue dval = builder.buildFrom(33);
        trans.add("abc", dval);
        boolean b = trans.commit();
        dumpErrors(trans);
        assertEquals(true, b);

        log("ss");
        MySimpleVisitor visitor = new MySimpleVisitor();
        Generator gen = dataSet.createGenerator();
        b = gen.generate(visitor);
        visitor.finish();
    }

    @Test
    public void testClone() {
        DataSet original = load("drawing1.dnal");
        assertEquals(3, original.size());

        DataSet dataSet = original.cloneEmptyDataSet();
        assertEquals(0, dataSet.size());
        Transaction trans = dataSet.createTransaction();
        IntBuilder builder = trans.createIntBuilder();
        DValue dval = builder.buildFrom(33);
        trans.add("abc", dval);
        boolean b = trans.commit();
        dumpErrors(trans);
        assertEquals(true, b);
        
        assertEquals(3, original.size());
        assertEquals(1, dataSet.size());
        
        log("ss");
        MySimpleVisitor visitor = new MySimpleVisitor();
        Generator gen = dataSet.createGenerator();
        b = gen.generate(visitor);
        visitor.finish();
    }

    private DataSet load(String dnalFilename) {
        XErrorTracker.logErrors = true;
        Log.debugLogging = true;

        DNALCompiler compiler = createCompiler();
        String path = GENERATE_DIR + dnalFilename;
        DataSet dataSet = compiler.compile(path, null);
        for(ErrorMessage err: compiler.getErrors()) {
            log(String.format("%d: %s", err.getLineNum(), err.getMessage()));
        }
        assertEquals(true, (dataSet != null));
        return dataSet;
    }

    private void dumpErrors(Transaction trans) {
        for(ErrorMessage err: trans.getValErrorList()) {
            log(err.getMessage());
        }
    }

}
