package org.dnal.compiler.parser;

import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.dnal.compiler.parser.ast.BooleanExp;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.RangeExp;
import org.dnal.compiler.parser.ast.RuleWithFieldExp;

public class RuleParser extends ParserBase {
    
	public static Parser<Exp> ruleT() {
		return Parsers.or(term("true")).retn(new BooleanExp(true));
	}
	public static Parser<Exp> ruleF() {
		return Parsers.or(term("false")).retn(new BooleanExp(false));
	}
	public static Parser<Exp> ruleOperand() {
		return Parsers.or(VarParser.someNumberValueassign(), strArg(), VarParser.ident(), ruleT(), ruleF());
	}
	
	
	//ruleFN
	public static Parser<Token> not() {
	    return term("!").optional();
	}
	
	public static Parser<RuleWithFieldExp> ruleName01() {
	    return Parsers.sequence(VarParser.ident(), term("."), VarParser.ident(), 
	            (IdentExp field, Token tok, IdentExp rule) -> new RuleWithFieldExp(rule, field));
	}
    @SuppressWarnings("unchecked")
    public static Parser<RuleWithFieldExp> ruleName02() {
        return Parsers.or(VarParser.ident())
                .map(new org.codehaus.jparsec.functors.Map<IdentExp, RuleWithFieldExp>() {
                    @Override
                    public RuleWithFieldExp map(IdentExp arg0) {
                        return new RuleWithFieldExp(arg0, null);
                    }
                });    
       }
    public static Parser<RuleWithFieldExp> ruleName() {
        return Parsers.or(ruleName01(), ruleName02());
    }
	
	public static Parser<CustomRule> ruleCustom01() {
		return Parsers.sequence(not(), ruleName(), term("("), ruleOperand().many().sepBy(term(",")), term(")"), 
				(Token notToken, RuleWithFieldExp exp1, Token tok, List<List<Exp>> arg, Token tok2) -> new CustomRule(exp1, arg, (notToken == null) ? null : notToken.toString()));
	}
	
//	public static Parser<CustomRule> ruleCustom02() {
//		return Parsers.sequence(not(), ruleName(), term("("), Parsers.or(ruleRange(), ruleSpaceRange()), term(")"), 
//				(Token notToken, RuleWithFieldExp exp1, Token tok, RangeExp range, Token tok2) -> new CustomRule(exp1, range, (notToken == null) ? null : notToken.toString()));
//	}
	
	
}