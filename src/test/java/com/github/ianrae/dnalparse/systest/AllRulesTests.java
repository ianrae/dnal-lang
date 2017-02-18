package com.github.ianrae.dnalparse.systest;

import org.junit.Test;

public class AllRulesTests extends SysTestBase {

    @Test
    public void testCompare() {
        String rule = "> 10";
        chkInt(rule, true);
        chkLong(rule, true);
        chkNumber(rule, true);
        chkBoolean(rule, false);
        
        
        /*
         *  INTEGER,
    LONG,
    NUMBER,
    BOOLEAN,
    STRING,
    DATE,
    LIST,
    //      MAP,
    STRUCT,
    ENUM

         */
    }
    
    
    private void chkBoolean(String rule, boolean pass) {
        if (pass) {
            chkRule(rule, "boolean", "true");
        } else {
            chkRuleFail(rule, "boolean", "true");
        }
    }
    private void chkInt(String rule, boolean pass) {
        if (pass) {
            chkRule(rule, "int", "11");
        } else {
            chkRuleFail(rule, "int", "11");
        }
    }
    private void chkLong(String rule, boolean pass) {
        if (pass) {
            chkRule(rule, "long", "11");
        } else {
            chkRuleFail(rule, "long", "11");
        }
    }
    private void chkNumber(String rule, boolean pass) {
        if (pass) {
            chkRule(rule, "number", "11.");
        } else {
            chkRuleFail(rule, "number", "11.");
        }
    }
    private void chkRule(String rule, String type, String value) {
        String source = String.format("type Foo %s %s end let x Foo = %s", type, rule, value);
        chkValue("x", source, 1, 1);
    }
    private void chkRuleFail(String rule, String type, String value) {
        String source = String.format("type Foo %s %s end let x Foo = %s", type, rule, value);
        load(source, false);
    }
    
}