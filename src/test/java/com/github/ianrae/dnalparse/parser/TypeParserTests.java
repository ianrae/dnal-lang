package com.github.ianrae.dnalparse.parser;

import static org.junit.Assert.assertEquals;

import org.codehaus.jparsec.Parsers;
import org.junit.Test;

import com.github.ianrae.dnalparse.parser.ast.Exp;

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
        Exp exp = parseInt("15");
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
    
    private Exp parseRange(String src) {
        Exp exp = Parsers.sequence(VarParser.someNumberValueassign(), VarParser.someNumberValueassign()).from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
        return exp;
    }

//    @Test
//    public void test1b() {
//        Exp exp = parseRange("15. 15.");
//        assertEquals("15", exp.strValue());
//    }
	
//    @Test
//    public void test2() {
//        String src = "15..20";
//        Exp exp = TypeParser.ruleRange().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(src);
//        assertEquals("15", exp.strValue());
//    }

}
