package org.dnal.compiler.core;

import org.dnal.compiler.nrule.IntegerRangeRule;
import org.dnal.core.nrule.virtual.VirtualInt;
import org.junit.Test;

public class ValidationCustomRuleTests extends BaseValidationTests {

	@Test
	public void testCustom() {
	    crf.addFactory(new MyCustomRule.Factory());
	    String decl = "rule myrule string";
		chkCustomRule(decl, "myrule(4)", "abc", true);
		chkCustomRule(decl, "!myrule(4)", "abc", false);
	}
    @Test
    public void testCustomBadDecl() {
        crf.addFactory(new MyCustomRule.Factory());
        generateOk = false;
        chkCustomRule("rule myrule date", "myrule(4)", "abc", false);
    }
    @Test
    public void testCustomMissingDecl() {
        crf.addFactory(new MyCustomRule.Factory());
        chkCustomRule("", "!myrule(4)", "abc", false);
    }
    
	@Test
	public void testInRule() {
		chkCustomIntRule("in(4,6,8)", 4, true);
		chkCustomIntRule("in(4,6,8)", 5, false);
		chkCustomIntRule("!in(4,6,8)", 5, true);
	}
    @Test
    public void testInStringRule() {
        chkCustomRule("", "in('a','b','e')", "b", true);
        chkCustomRule("", "in('a','b','e')", "e", true);
        chkCustomRule("", "in('a','b','e')", "f", false);
        chkCustomRule("", "!in('a','b','e')", "f", true);
    }
    
	
	@Test
	public void testRangeRule() {
		VirtualInt vs = new VirtualInt();
		IntegerRangeRule rule = new IntegerRangeRule("range", vs);
		
		chkCustomIntRule("range(4..8)", 4, true);
		chkCustomIntRule("range(4..8)", 8, false);
		chkCustomIntRule("!range(4..8)", 8, true);
	}
	
    @Test
    public void testEmptyRule() {
        String decl = "";
        chkCustomRule(decl, "empty()", "abc", false);
        chkCustomRule(decl, "empty()", "", true);
        chkCustomRule(decl, "!empty()", "", false);
    }
    @Test
    public void testRegex() {
        String decl = "";
        chkCustomRule(decl, "regex('a*b')", "aab", true);
        chkCustomRule(decl, "regex('a*b')", "axb", false);
        chkCustomRule(decl, "regex(\"a*b\")", "axb", false);
        chkCustomRule(decl, "regex('a*\"b')", "aa\"b", true);
    }
    @Test
    public void testStartsWith() {
        String decl = "";
        chkCustomRule(decl, "startsWith('ab')", "abb", true);
        chkCustomRule(decl, "startsWith('ab')", "ABb", false);
        chkCustomRule(decl, "startsWith('ab')", "", false);
        chkCustomRule(decl, "startsWith('')", "", true);
    }
    @Test
    public void testEndsWith() {
        String decl = "";
        chkCustomRule(decl, "endsWith('ab')", "cab", true);
        chkCustomRule(decl, "endsWith('ab')", "zzAB", false);
        chkCustomRule(decl, "endsWith('ab')", "", false);
        chkCustomRule(decl, "endsWith('')", "", true);
    }
    @Test
    public void testContains() {
        String decl = "";
        chkCustomRule(decl, "contains('ab')", "cab", true);
        chkCustomRule(decl, "contains('ab')", "CAB", false);
    }
	
	
	private void chkCustomRule(String decl, String rule, String str, boolean ok) {
        expected = (decl.isEmpty()) ? 2 : 3;
		String s = String.format("%s type Foo string %s end let x Foo = '%s'", decl, rule, str);
		parseAndValidate(s, ok, "STRING_SHAPE");
	}
	private void chkCustomIntRule(String text, int n, boolean ok) {
		String s = String.format("type Foo int %s end let x Foo = %d", text, n);
		parseAndValidate(s, ok, "INTEGER_SHAPE");
	}

}
