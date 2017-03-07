package com.github.ianrae.dnalparse.systest;

import static org.junit.Assert.assertEquals;

import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.junit.Test;

public class ViaSysTests extends SysTestBase {

    //TYPE 1 - VIA-1
    @Test
    public void test500() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "type Person struct { name string addr Address } end ";
        String src3 = "let x Person = { 'abc', { 'main st', 'Kingston' }  }";
        DValue dval = chkValue("x", src1 + src2 + src3, 2, 1);

        DStructHelper helper = new DStructHelper(dval);
        assertEquals("abc", helper.getField("name").asString());
        DStructHelper helper2 = new DStructHelper(helper.getField("addr"));
        assertEquals("Kingston", helper2.getField("city").asString());
        this.registry.dump();
        this.world.dump();
    }

    //TYPE 2 - VIA-MULTI
    @Test
    public void test501() {
        String src1 = "type Address struct { street string city string} end ";
        String src2 = "type Person struct { name string addr Address } end ";
        String src3 = "let a10 Address = { 'queen st', 'Ottawa' }";
        String src4 = "let x Person = { 'abc', via city 'Ottawa'  }";
        DValue dval = chkValue("x", src1 + src2 + src3 + src4, 2, 2);

        DStructHelper helper = new DStructHelper(dval);
        assertEquals("abc", helper.getField("name").asString());
        DStructHelper helper2 = new DStructHelper(helper.getField("addr"));
        assertEquals("Ottawa", helper2.getField("city").asString());
        this.registry.dump();
        this.world.dump();
    }

    //TYPE 3 - ISA-1 
    @Test
    public void test510() {
        sb = new StringBuilder();
        o("type Address struct { id string city string} end ");
        o("type Person struct { name string addrId string } addrId isa Address.id end ");
        o("let a10 Address = { 'a5', 'Ottawa' }");
        o("let x Person = { 'abc', 'a5'  }");
        DValue dval = chkValue("x", sb.toString(), 2, 2);

        DStructHelper helper = new DStructHelper(dval);
        assertEquals("abc", helper.getField("name").asString());
        assertEquals("a5", helper.getField("addrId").asString());
        this.registry.dump();
        this.world.dump();
    }

    //TYPE 3 - ISA-1 fail
    @Test
    public void test511() {
        //isa is validate only
        sb = new StringBuilder();
        o("type Address struct { id string city string} end ");
        o("type Person struct { name string addrId string } addrId isa Address.id end ");
        o("let a10 Address = { 'a5', 'Ottawa' }");
        o("let x Person = { 'abc', 'a6'  }");
        chkFail(sb.toString(), 1, "isa:");
    }
//    TYPE 4 - ISA-MULTI
    @Test
    public void test512() {
        //isa is validate only
        sb = new StringBuilder();
        o("type Address struct { id string personCode string} personCode isa Person.code end ");
        o("type Person struct { code string addrIds list<string> } addrIds isa Address.id end ");
        o("let a10 Address = { 'a5', 'p5' }");
        o("let a11 Address = { 'a6', 'p6' }");
        o("let a12 Address = { 'a7', 'p5' }");
        o("let x Person = { 'p5', [ 'a5', 'a7' ]  }");
        o("let x2 Person = { 'p6', [ ]  }");
        DValue dval = chkValue("x", sb.toString(), 3, 5);
        chkList(dval, "addrIds", 2, "a5", "a7");
    }

    //TYPE 2 - VIA-MULTI
    @Test
    public void test513() {
        sb = new StringBuilder();
        o("type Address struct { id string personCode string} personCode isa Person.code end ");
        o("type Person struct { code string addrs list<Address> }  end ");
        o("let a10 Address = { 'a5', 'p5' }");
        o("let a11 Address = { 'a6', 'p6' }");
        o("let a12 Address = { 'a7', 'p5' }");
        o("let x Person = { 'p5', [ via personCode code ]  }");
        o("let x2 Person = { 'p6', [ ]  }");
        DValue dval = chkValue("x", sb.toString(), 3, 5);
        DStructHelper helper = new DStructHelper(dval);
        DValue field = helper.getField("addrs");
        assertEquals(2, field.asList().size());
        DStructHelper h2 = new DStructHelper(field.asList().get(0));
        DValue ff = h2.getField("id");
        assertEquals("a5", ff.asString());
        h2 = new DStructHelper(field.asList().get(0));
        ff = h2.getField("id");
        assertEquals("a5", ff.asString());
        
    }

    //TYPE 5 - BOTH-1
    private String buildBoth1() {
        sb = new StringBuilder();
        o("type Address struct { id string personCode string} personCode isa Person.code end ");
        o("type Person struct { code string addrId string } addrId isa Address.id end ");
        o("let a10 Address = { 'a5', 'p5' }");
        o("let a11 Address = { 'a6', 'p6' }");
        o("let a12 Address = { 'a7', 'p6' }");
        o("let x Person = { 'p5', via personCode code  }");
        o("let x2 Person = { 'p6', 'a6'  }");
        return sb.toString();
    }
    @Test
    public void test515() {
        String src = buildBoth1();
        DValue dval = chkValue("x", src, 2, 5);
        DStructHelper helper = new DStructHelper(dval);
        DValue field = helper.getField("addrId");
        assertEquals("a5", field.asString());
    }
    @Test
    public void test515fail1() {
        String src = buildBoth1();
        src = src.replace("personCode isa Person.code", "xxpersonCode isa Person.code"); //wrong field
        chkFail(src, 3, "validation error: VALIDATION_ERROR: null- failed");
    }
    @Test
    public void test515fail2() {
        String src = buildBoth1();
        src = src.replace("personCode isa Person.code", "personCode isa xxPerson.code"); //wrong field
        chkFail(src, 6, "validation error: VALIDATION_ERROR: null- failed");
    }
    @Test
    public void test515fail3() {
        String src = buildBoth1();
        src = src.replace("personCode isa Person.code", "personCode isa Person.xxcode"); //wrong field
        chkFail(src, 9, "validation error: VALIDATION_ERROR: isa: null");
    }
    
    //TYPE 6 - BOTH-MULTI
    private String buildBothMulti() {
        sb = new StringBuilder();
        o("type Address struct { id string personCode string} personCode isa Person.code end ");
        o("type Person struct { code string addrIds list<string> } addrIds isa Address.id end ");
        o("let a10 Address = { 'a5', 'p5' }");
        o("let a11 Address = { 'a6', 'p6' }");
        o("let a12 Address = { 'a7', 'p5' }");
        o("let x Person = { 'p5', [ via personCode code ]  }");
        o("let x2 Person = { 'p6', [ ]  }");
        return sb.toString();
    }
    @Test
    public void test514() {
        String src = buildBothMulti();
        DValue dval = chkValue("x", sb.toString(), 3, 5);
        chkList(dval, "addrIds", 2, "a5", "a7");
    }
    @Test
    public void test514fail1() {
        String src = buildBothMulti();
        src = src.replace("[ via personCode code ]", "via personCode code"); //leave off [ ]
        chkFail(src, 2, "Dcan't resolve via: personCode: code");
    }
    @Test
    public void test514fail2() {
        String src = buildBothMulti();
        src = src.replace("[ via personCode code ]", "[ isa personCode code ]"); //isa instead of via
        chkFail(src, 1, "expected, isa encountered");
    }
    @Test
    public void test514fail3() {
        String src = buildBothMulti();
        src = src.replace("[ via personCode code ]", "[ via xxxpersonCode code ]"); //invalid field name
        chkFail(src, 2, "Acan't resolve via: xxxpersonCode");
    }
    @Test
    public void test514fail4() {
        String src = buildBothMulti();
        src = src.replace("[ via personCode code ]", "[ via personCode xxxcode ]"); //invalid field name
        chkFail(src, 5, "Acan't resolve via: personCode");
    }
    

    //-----------------------
    private StringBuilder sb;

    private void o(String s) {
        sb.append(s);
    }
    
    private void chkList(DValue dval, String fieldName, int expected, String s1, String s2) {
        DStructHelper helper = new DStructHelper(dval);
        DValue field = helper.getField(fieldName);
        assertEquals(expected, field.asList().size());
        if (expected > 0) {
            assertEquals(s1, field.asList().get(0).asString());
        }
        
        if (expected > 1) {
            assertEquals(s2, field.asList().get(1).asString());
        }
    }
}
