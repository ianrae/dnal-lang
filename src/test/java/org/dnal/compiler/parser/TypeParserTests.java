package org.dnal.compiler.parser;

import static org.junit.Assert.assertEquals;

import org.codehaus.jparsec.Parsers;
import org.dnal.compiler.parser.TerminalParser;
import org.dnal.compiler.parser.TypeParser;
import org.dnal.compiler.parser.VarParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.RangeExp;
import org.junit.Test;

public class TypeParserTests {
    
    private Exp parseInt(String src) {
        Exp exp = TypeParser.intArg().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
        return exp;
    }
    private Exp parseSomeNum(String src) {
        Exp exp = VarParser.someNumberValueassign().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
        return exp;
    }

    @Test
    public void test1() {
        Exp exp = parseInt("15 ");
        assertEquals("15", exp.strValue());
    }
    @Test
    public void test1a() {
        Exp exp = parseSomeNum("15");
        assertEquals("15", exp.strValue());
        exp = parseSomeNum("15.5");
        assertEquals("15.5", exp.strValue());
        exp = parseSomeNum("15.");
        assertEquals("15.0", exp.strValue());
    }
    
	
    @Test
    public void test2() {
        String src = "15..20";
        Exp exp = TypeParser.ruleRange().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
        RangeExp rexp = (RangeExp)exp;
        assertEquals(15, rexp.from.intValue());
        assertEquals(20, rexp.to.intValue());
    }
    @Test
    public void test2a() {
        String src = "15 ..20";
        Exp exp = TypeParser.ruleSpaceRange().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
        RangeExp rexp = (RangeExp)exp;
        assertEquals(15, rexp.from.intValue());
        assertEquals(20, rexp.to.intValue());
    }

}
