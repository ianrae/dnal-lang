package com.github.ianrae.dnalparse.parser;

import static org.junit.Assert.assertEquals;

import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.RuleDeclExp;
import org.junit.Test;

public class RuleDeclTests {
    
    @Test
    public void test1() {
        RuleDeclExp ax = (RuleDeclExp) FullParser.parse02("rule postalcode string");
        assertEquals("postalcode", ax.ruleName);
        assertEquals("string", ax.ruleType);
    }

}
