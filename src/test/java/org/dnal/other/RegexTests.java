package org.dnal.other;
import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;


public class RegexTests {

    public static class XRule {
        private String regexPattern;
        private Pattern compiled;
        
        public XRule(String pattern) {
            this.regexPattern = pattern;
            this.compiled = Pattern.compile(pattern);
        }
        public boolean eval(String input) {
            Matcher m = compiled.matcher(input);
            return m.matches();
        }
    }

    @Test
    public void test() {
        Pattern p = Pattern.compile("a*b");
        Matcher m = p.matcher("aaaaab");
        boolean b = m.matches();
        assertEquals(true, b);
     }
    @Test
    public void testx() {
        XRule xrule = new XRule("a*b");
        assertEquals(true, xrule.eval("aaab"));
        assertEquals(true, xrule.eval("ab"));
        assertEquals(false, xrule.eval("cb"));
     }

}
