package com.github.ianrae.dnalparse.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;

import com.github.ianrae.dnalparse.parser.ast.IdentExp;
import com.github.ianrae.dnalparse.parser.ast.RuleDeclExp;

public class RuleDeclParser extends ParserBase {

    
    public static Parser<IdentExp> custom() {
        return Parsers.sequence(term("rule"), VarParser.ident(), 
                (Token tok1, IdentExp exp1) -> exp1);
    }
    
    public static Parser<RuleDeclExp> customRuleDecl() {
        return Parsers.sequence(custom(), VarParser.ident(), 
                (IdentExp ruleNameExp, IdentExp ruleTypeExp) -> new RuleDeclExp(ruleNameExp, ruleTypeExp));
    }
    
}