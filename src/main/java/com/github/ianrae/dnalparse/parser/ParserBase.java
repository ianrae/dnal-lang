package com.github.ianrae.dnalparse.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Token;

import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.IntegerExp;
import com.github.ianrae.dnalparse.parser.ast.StringExp;

public class ParserBase {
    public static Parser<Token> term(String name) {
        return TerminalParser.token(name);
    }   
    
    
    public static Parser<Exp> intArg() {
        return TerminalParser.numberSyntacticParser
        .map(new org.codehaus.jparsec.functors.Map<String, IntegerExp>() {
            @Override
            public IntegerExp map(String arg) {
                Integer nval = Integer.parseInt(arg);

                return new IntegerExp(nval);
            }
        });
    }
    public static Parser<Exp> strArg() {
        return TerminalParser.stringSyntacticParser
        .map(new org.codehaus.jparsec.functors.Map<String, StringExp>() {
            @Override
            public StringExp map(String arg) {
                return new StringExp(arg);
            }
        });
    }

}
