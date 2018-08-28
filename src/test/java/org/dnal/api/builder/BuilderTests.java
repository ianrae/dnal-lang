package org.dnal.api.builder;

import static org.junit.Assert.*;

import org.dnal.api.Transaction;
import org.dnal.api.systest.SysTestBase;
import org.dnal.core.DValue;
import org.dnal.core.builder.IntBuilder;
import org.dnal.core.builder.StringBuilder;
import org.dnal.core.builder.StructBuilder;
import org.junit.Test;

public class BuilderTests extends SysTestBase {

    @Test
    public void testString() {
        chk("type Person struct { name string } end", 1, 0);
        Transaction trans = this.dataSetLoaded.createTransaction();
        StringBuilder builder = trans.createStringBuilder();
        DValue dval = builder.buildFromString(null);
        assertEquals(false, builder.wasSuccessful());
        assertEquals(null, dval);
        
        builder = trans.createStringBuilder();
        dval = builder.buildFromString("");
        assertEquals(true, builder.wasSuccessful());
        assertEquals("", dval.asString());
        
        builder = trans.createStringBuilder();
        dval = builder.buildFromString("abc");
        assertEquals(true, builder.wasSuccessful());
        assertEquals("abc", dval.asString());
    }
    
    @Test
    public void testStruct1() {
        chk("type Person struct { name string } end", 1, 0);
        Transaction trans = this.dataSetLoaded.createTransaction();
        StructBuilder structBuilder = trans.createStructBuilder("Person");
        StringBuilder builder = trans.createStringBuilder();
        DValue dval = builder.buildFromString(null);
        assertEquals(null, dval);
        assertEquals(false, builder.wasSuccessful());
        structBuilder.addField("name", dval);
        DValue dvalStruct = structBuilder.finish();
        assertNotNull(dvalStruct);
        assertEquals(false, builder.wasSuccessful());
    }
    
    @Test
    public void testStruct2() {
        chk("type Person struct { name string } end", 1, 0);
        Transaction trans = this.dataSetLoaded.createTransaction();
        StructBuilder structBuilder = trans.createStructBuilder("Person");
        StringBuilder builder = trans.createStringBuilder();
        DValue dval = builder.buildFromString("");
        assertNotNull(dval);
        assertEquals(true, builder.wasSuccessful());
        structBuilder.addField("name", dval);
        DValue dvalStruct = structBuilder.finish();
        assertNotNull(dvalStruct);
        assertEquals(true, builder.wasSuccessful());
    }
    
    @Test
    public void testInt() {
        chk("type Person struct { name string } end", 1, 0);
        Transaction trans = this.dataSetLoaded.createTransaction();
        IntBuilder builder = trans.createIntBuilder();
        DValue dval = builder.buildFromString(null);
        assertEquals(false, builder.wasSuccessful());
        assertEquals(null, dval);
        
        builder = trans.createIntBuilder();
        dval = builder.buildFromString("");
        assertNull(dval);
        assertEquals(false, builder.wasSuccessful());
        
        builder = trans.createIntBuilder();
        dval = builder.buildFromString("23");
        assertEquals(true, builder.wasSuccessful());
        assertEquals(23, dval.asInt());
    }
    
    @Test
    public void testStruct3() {
        chk("type Person struct { name int } end", 1, 0);
        Transaction trans = this.dataSetLoaded.createTransaction();
        StructBuilder structBuilder = trans.createStructBuilder("Person");
        IntBuilder builder = trans.createIntBuilder();
        DValue dval = builder.buildFromString("23");
        assertNotNull(dval);
        assertEquals(true, builder.wasSuccessful());
        structBuilder.addField("name", dval);
        DValue dvalStruct = structBuilder.finish();
        assertNotNull(dvalStruct);
        assertEquals(true, builder.wasSuccessful());
    }
    
    @Test
    public void testStruct4() {
        chk("type Person struct { name int } end", 1, 0);
        Transaction trans = this.dataSetLoaded.createTransaction();
        StructBuilder structBuilder = trans.createStructBuilder("Person");
        IntBuilder builder = trans.createIntBuilder();
        DValue dval = builder.buildFromString("");
        assertNull(dval);
        assertEquals(false, builder.wasSuccessful());
        DValue dvalStruct = structBuilder.finish();
        assertNotNull(dvalStruct);
        assertEquals(false, builder.wasSuccessful());
    }
    
    @Test
    public void testStruct5() {
        chk("type Person struct { name int } end", 1, 0);
        Transaction trans = this.dataSetLoaded.createTransaction();
        StructBuilder structBuilder = trans.createStructBuilder("Person");
        structBuilder.addField("name", "");
        DValue dvalStruct = structBuilder.finish();
        assertNotNull(dvalStruct);
        assertEquals(false, structBuilder.wasSuccessful());
    }
    
}
