package org.dnal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.dnal.compiler.parser.ast.BooleanExp;
import org.dnal.compiler.parser.ast.ComparisonAndRuleExp;
import org.dnal.compiler.parser.ast.ComparisonOrRuleExp;
import org.dnal.compiler.parser.ast.ComparisonRuleExp;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.IsaRuleExp;
import org.dnal.compiler.parser.ast.RangeExp;
import org.dnal.compiler.parser.ast.RuleExp;
import org.dnal.compiler.parser.ast.RuleWithFieldExp;

public class RuleParser extends ParserBase {
    
	@SuppressWarnings("unchecked")
	public static Parser<Exp> ruleT() {
		return Parsers.or(term("true")).retn(new BooleanExp(true));
	}
	@SuppressWarnings("unchecked")
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
	
	public static Parser<CustomRule> ruleFn1() {
		return Parsers.sequence(not(), ruleName(), term("("), ruleOperand().many().sepBy(term(",")), term(")"), 
				(Token notToken, RuleWithFieldExp exp1, Token tok, List<List<Exp>> arg, Token tok2) -> new CustomRule(exp1, arg, (notToken == null) ? null : notToken.toString()));
	}
	
	public static Parser<CustomRule> ruleFn() {
		return Parsers.or(ruleFn1(), ruleCustom02());
	}
	

	/*
	 * The parser gets confused with "15..20" and sees "15.". The workaround is
	 * to accept 15. followed by another .
	 */
    public static Parser<Exp> intWithDotArg() {
        return TerminalParser.numberSyntacticParser
        .map(new org.codehaus.jparsec.functors.Map<String, IntegerExp>() {
            @Override
            public IntegerExp map(String arg) {
                if (arg != null && ! arg.endsWith(".")) {
                    throw new IllegalArgumentException("intWithDotArg: invalid " + arg);
                }
                Integer nval = Integer.parseInt(arg.substring(0, arg.length() - 1));

                return new IntegerExp(nval);
            }
        });
    }
	    //handles 15..20
	public static Parser<RangeExp> ruleRange() {
		return Parsers.sequence(intWithDotArg().followedBy(term(".")), intArg(), 
				(Exp exp1, Exp exp2) -> new RangeExp(exp1, exp2));
	}
	
	//handles 15 .. 20
    public static Parser<RangeExp> ruleSpaceRange() {
        return Parsers.sequence(intArg(), term(".."), intArg(), 
                (Exp exp1, Token dotdot, Exp exp2) -> new RangeExp(exp1, exp2));
    }
	
	
	public static Parser<CustomRule> ruleCustom02() {
		return Parsers.sequence(not(), ruleName(), term("("), Parsers.or(ruleRange(), ruleSpaceRange()), term(")"), 
				(Token notToken, RuleWithFieldExp exp1, Token tok, RangeExp range, Token tok2) -> new CustomRule(exp1, range, (notToken == null) ? null : notToken.toString()));
	}
	
	public static Parser<Exp> ruleOperand1() {
		return Parsers.or(ruleFn(), ruleOperand());
	}
	
	//isa
    public static Parser<IsaRuleExp> isaDecl() {
        return Parsers.sequence(VarParser.ident().optional(), term("isa"), 
                VarParser.ident().many().sepBy(term(".")), 
                (IdentExp exp, Token tok, List<List<IdentExp>> arg)
                -> new IsaRuleExp(exp, arg));
    }    
	
	
	//ruleExpr
    public static Parser<Exp> optionalRuleArg() {
    	return ruleOperand1().optional();
    }
    
	public static Parser<RuleExp> rule0() {
		return Parsers.sequence(optionalRuleArg(), 
		        Parsers.or(term("<"), term(">"), term(">="), term("<="), term("=="), term("!=")), 
		        ruleOperand1(), 
				(Exp optArg, Token optok, Exp numExp) -> new ComparisonRuleExp(optArg, optok.toString(), numExp));
	}
	public static Parser<RuleExp> ruleOr() {
		return Parsers.sequence(rule0(), term("or"), rule0(), 
				(Exp exp1, Token ortok, Exp exp2) -> new ComparisonOrRuleExp((ComparisonRuleExp)exp1, (ComparisonRuleExp)exp2));
	}
	public static Parser<RuleExp> ruleAnd() {
		return Parsers.sequence(rule0(), term("and"), rule0(), 
				(Exp exp1, Token ortok, Exp exp2) -> new ComparisonAndRuleExp((ComparisonRuleExp)exp1, (ComparisonRuleExp)exp2));
	}
	
	public static Parser<RuleExp> ruleExpr() {
		return Parsers.or(ruleAnd(), ruleOr(), rule0(), ruleFn(), isaDecl());
	}
	
	
	//rules
	public static Parser<List<RuleExp>> ruleMany() {
        return ruleExpr().many().sepBy(term(","))
                .map(new org.codehaus.jparsec.functors.Map<List<List<RuleExp>>, List<RuleExp>>() {
                    @Override
                    public List<RuleExp> map(List<List<RuleExp>> arg0) {
						List<RuleExp> cc = new ArrayList<>();
						for(List<RuleExp> sub: arg0) {
							if (sub.size() > 1) {
								throw new IllegalArgumentException("Rules must be separated by commas");
							}
							for(RuleExp re: sub) {
								cc.add(re);
							}
						}
						return cc;
                    }
                });    
	}
	
	
}