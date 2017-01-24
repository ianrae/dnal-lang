package com.github.ianrae.dnalparse.systest;

import static org.junit.Assert.assertEquals;

import org.dnal.api.DNALCompiler;
import org.dnal.core.DValue;
import org.junit.Test;

import com.github.ianrae.dnalparse.compiler.MyCustomRule;

public class RulesSysTests extends SysTestBase {

    @Test
    public void testT100() {
        chkRules("");
        chkRules("< 100");
        chkRules("> 0 < 100");
        
        chkRules("== 14");
        chkRules("!= 5");
        chkRules("<= 100");
        chkRules(">= 5");
    }
    @Test
    public void testT101() {
        chkRulesFail("> 20 < 100", "RULEFAIL: > 20: > 20");
    }
    @Test
    public void testT102a() {
        chkLongRules("");
        chkLongRules("< 100");
        chkLongRules("> 0 < 100");
        
        chkLongRules("== 14");
        chkLongRules("!= 5");
        chkLongRules("<= 100");
        chkLongRules(">= 5");
    }
    
    @Test
    public void testT102() {
        DNALCompiler compiler = createCompiler();
        compiler.registryRuleFactory(new MyCustomRule.Factory());
        String fmt = "type Foo string myrule(44) end let x Foo = '%s'";
        load(String.format(fmt, "abc"), true, compiler);
        DValue dval = findValue("x");
        assertEquals("abc", dval.asString());
        
        load(String.format(fmt, "ddd"), false, compiler);
    }
    
    
    private void chkRules(String rules) {
        String source = String.format("type Foo int %s end let x Foo = 14", rules);
        DValue dval = chkValue("x", source, 1, 1);
        assertEquals(14, dval.asInt());
    }
    private void chkRulesFail(String rules, String errMsg) {
        String source = String.format("type Foo int %s end let x Foo = 14", rules);
        chkFail(source, 1, errMsg);
    }
    private void chkLongRules(String rules) {
        String source = String.format("type Foo long %s end let x Foo = 14", rules);
        DValue dval = chkValue("x", source, 1, 1);
        assertEquals(14L, dval.asLong());
    }
    
}