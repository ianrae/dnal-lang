package org.dnal.compiler.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.RuleDeclExp;

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