package com.github.ianrae.dnalparse.systest;

import org.junit.Test;

/*
 * TODO
 * -one test for each rule
 * -then another source file for struct members
 */

public class AllRulesTests extends SysTestBase {

    @Test
    public void test0() {
//        chkString("> 10", false);
//        chkList("> 10", false);
//        chkEnum("> 'RED'", false); //should RED not 'RED'
//        chkString("== 'abc'", true);
//        chkEnum("== RED", true);
//        chkLong("in(11, 12)", true);
        chkNumber("in(11.1, 12.5)", true);
        
        
        
    }
    @Test
    public void testCompare() {
        chkInt("> 10", true);
        chkLong("> 10", true);
        chkNumber("> 10.0", true);
        chkBoolean("> 10", false);
        chkString("> 'abc'", false);
        chkDate("> '2013'", true);
//        chkList("> 10", false); !!need to disallow rules for list
        chkEnum("> RED", false);
    }
    
    @Test
    public void testEq() {
        chkInt("== 11", true);
        chkLong("== 11", true);
        chkNumber("== 11.1", true);
        chkBoolean("== 10", false);
        chkString("== 'abc'", true);
        chkDate("== '2015'", true);
////        chkList("== 10", false); !!need to disallow rules for list
        chkEnum("== RED", true);
    }
    
    @Test
    public void testiEq() {
        chkInt("ieq(11)", false);
        chkLong("ieq(11)", false);
        chkNumber("ieq(11.1)", false);
        chkBoolean("ieq(10)", false);
        chkString("ieq('abc')", true);
        chkDate("ieq('2015')", false);
////        chkList("ieq(10)", false); !!need to disallow rules for list
        chkEnum("ieq(RED)", false);
    }
    
    @Test
    public void testIn() {
        chkInt("in(11, 12)", true);
        chkLong("in(11, 12)", true);
        chkNumber("in(11.1, 12.5)", true);
        chkBoolean("in(10)", false);
        chkString("in('abc')", true);
        chkDate("in('2015')", false);
////        chkList("in(10)", false); !!need to disallow rules for list
        chkEnum("in(RED)", false);
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
            chkRule(rule, "number", "11.1");
        } else {
            chkRuleFail(rule, "number", "11.1");
        }
    }
    private void chkString(String rule, boolean pass) {
        if (pass) {
            chkRule(rule, "string", "'abc'");
        } else {
            chkRuleFail(rule, "string", "'abc'");
        }
    }
    private void chkDate(String rule, boolean pass) {
        if (pass) {
            chkRule(rule, "date", "'2015'");
        } else {
            chkRuleFail(rule, "date", "'2015'");
        }
    }
    private void chkList(String rule, boolean pass) {
        if (pass) {
            chkRule(rule, "list<string>", "['2015']");
        } else {
            chkRuleFail(rule, "list<string>", "['2015']");
        }
    }
    private void chkEnum(String rule, boolean pass) {
//        String senum = "type X enum { RED BLUE GREEN } end";
//        String source = String.format("%s type Foo struct { col X } %s end let x Foo = {%s}", senum, rule, "'RED'");
        String fmt = "type X enum { RED BLUE GREEN } %s end let x X = %s";
        String source = String.format(fmt, rule, "RED");
        
        if (pass) {
            chkValue("x", source, 1, 1);
        } else {
            load(source, false);
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