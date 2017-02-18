package com.github.ianrae.dnalparse.systest;

import org.junit.Test;

/*
 * -one test for each rule
 * -then another source file for struct members
 */

public class AllRulesStructTests extends SysTestBase {

    @Test
    public void test0() {
//        chkDate("fld == '2015'", false);  //must test exact date
//      chkDate("fld == '2001-07-04T12:08:56.235-0700'", false, "'2001-07-04T12:08:56.235-0700'");  //must test exact date
//      chkBoolean("fld > 10", false);
//      chkDate("fld > '2013'", true);
//      chkString("fld > 'abc'", false);
//      chkBoolean("fld == 10", false);
//      chkList("fld.contains('2015')", true);
      chkEnum("fld.contains(RED)", true);

    }
    
    @Test
    public void testCompare() {
        chkInt("fld > 10", true);
        chkLong("fld > 10", true);
        chkNumber("fld > 10.0", true);
        chkBoolean("fld > 10", false);
        chkString("fld > 'abc'", false);
        chkDate("fld > '2013'", true);
        chkList("fld > 10", false); 
        chkEnum("fld > RED", false);
    }
    
    @Test
    public void testEq() {
        chkInt("fld == 11", true);
        chkLong("fld == 11", true);
        chkNumber("fld == 11.1", true);
        chkBoolean("fld == 10", false);
        chkString("fld == 'abc'", true);
        chkDate("fld == '2001-07-04T12:08:56.235-0700'", false, "'2001-07-04T12:08:56.235-0700'");  //must test exact date
        chkList("fld == 10", false); 
        chkEnum("fld == RED", true);
    }
    
    @Test
    public void testiEq() {
        chkInt("fld.ieq(11)", false);
        chkLong("fld.ieq(11)", false);
        chkNumber("fld.ieq(11.1)", false);
        chkBoolean("fld.ieq(10)", false);
        chkString("fld.ieq('abc')", true);
        chkDate("fld.ieq('2015')", false);
        chkList("fld.ieq(10)", false); 
        chkEnum("fld.ieq(RED)", false);
    }
    
    @Test
    public void testIn() {
        chkInt("fld.in(11, 12)", true);
        chkLong("fld.in(11, 12)", true);
        chkNumber("fld.in(11.1, 12.5)", true);
        chkBoolean("fld.in(10)", false);
        chkString("fld.in('abc')", true);
        chkDate("fld.in('2015')", false);
        chkList("fld.in(10)", false); 
        chkEnum("fld.in(RED)", false);
    }
    
    @Test
    public void testRange() {
        chkInt("fld.range(11, 12)", true);
        chkLong("fld.range(11, 12)", true);
        chkNumber("fld.range(11.1, 12.5)", true);
        chkBoolean("fld.range(10)", false);
        chkString("fld.range('abc', 'def')", true);
        chkDate("fld.range('2015', '2016')", true);
        chkList("fld.range(10)", false); 
        chkEnum("fld.range(RED)", false);
    }
    
    @Test
    public void testContains() {
        chkInt("fld.contains(11)", false);
        chkLong("fld.contains(11)", false);
        chkNumber("fld.contains(11.1)", false);
        chkBoolean("fld.contains(10)", false);
        chkString("fld.contains('abc')", true);
        chkDate("fld.contains('2015')", false);
        chkList("fld.contains('2015')", true);
        chkEnum("fld.contains(RED)", true);
    }
//    @Test
//    public void testEmpty() {
//        chkInt("empty()", false);
//        chkLong("empty()", false);
//        chkNumber("empty()", false);
//        chkBoolean("empty()", false);
//        chkString("empty()", true, "''");
//        chkDate("empty()", false);
//        chkList("empty()", true, "[]");
//        chkEnum("empty()", false);
//    }
//    @Test
//    public void testRegex() {
//        chkInt("regex('a')", false);
//        chkLong("regex('a')", false);
//        chkNumber("regex('a')", false);
//        chkBoolean("regex('a')", false);
//        chkString("regex('a*b')", true, "'aab'");
//        chkDate("regex('a')", false);
//        chkList("regex('a')", false, "[]");
//        chkEnum("regex('a')", false);
//    }
//    
//    //isa not tested here
//    
//    @Test
//    public void testStartsWith() {
//        chkInt("startsWith('a')", false);
//        chkLong("startsWith('a')", false);
//        chkNumber("startsWith('a')", false);
//        chkBoolean("startsWith('a')", false);
//        chkString("startsWith('aaZ')", true, "'aaZb'");
//        chkDate("startsWith('a')", false);
//        chkList("startsWith('a')", false, "[]");
//        chkEnum("startsWith('a')", false);
//    }
//    @Test
//    public void testEndsWith() {
//        chkInt("endsWith('a')", false);
//        chkLong("endsWith('a')", false);
//        chkNumber("endsWith('a')", false);
//        chkBoolean("endsWith('a')", false);
//        chkString("endsWith('aaZ')", true, "'zzaaZ'");
//        chkDate("endsWith('a')", false);
//        chkList("endsWith('a')", false, "[]");
//        chkEnum("endsWith('a')", false);
//    }
    
    //-------------
    private int expectedTypes = 1;
    
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
        chkString(rule, pass, "'abc'");
    }
    private void chkString(String rule, boolean pass, String value) {
        if (pass) {
            chkRule(rule, "string", value);
        } else {
            chkRuleFail(rule, "string", value);
        }
    }
    private void chkDate(String rule, boolean pass) {
        chkDate(rule, pass, "'2015'");
    }
    private void chkDate(String rule, boolean pass, String value) {
        if (pass) {
            chkRule(rule, "date", value);
        } else {
            chkRuleFail(rule, "date", value);
        }
    }
    private void chkList(String rule, boolean pass) {
        chkList(rule, pass, "['2015']");
    }
    private void chkList(String rule, boolean pass, String value) {
        expectedTypes = 2;
        if (pass) {
            chkRule(rule, "list<string>", value);
        } else {
            chkRuleFail(rule, "list<string>", value);
        }
        expectedTypes = 1; //reset
    }
    private void chkEnum(String rule, boolean pass) {
        String senum = "type X enum { RED BLUE GREEN } end";
        String source = String.format("%s type Foo struct { fld X } %s end let x Foo = {%s}", senum, rule, "RED");
        if (pass) {
            chkValue("x", source, 2, 1);
        } else {
            load(source, false);
        }
    }

    private void chkRule(String rule, String type, String value) {
        String source = String.format("type Foo struct { fld %s } %s end let x Foo = { %s }", type, rule, value);
        chkValue("x", source, expectedTypes, 1);
    }
    private void chkRuleFail(String rule, String type, String value) {
        String source = String.format("type Foo struct { fld %s } %s end let x Foo = { %s }", type, rule, value);
        load(source, false);
    }
    
}