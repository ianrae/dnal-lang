package org.dnal.compiler.parser;

import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.dnal.compiler.parser.ast.ComparisonRuleExp;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.RuleExp;
import org.dnal.compiler.parser.ast.ViewExp;
import org.dnal.compiler.parser.ast.ViewMemberExp;

public class ViewParser extends ParserBase {
	
	public static Parser<Token> direction() {
		return Parsers.or(term("<-"), term("->")); 
	}
    
	public static Parser<ViewMemberExp> member() {
		return Parsers.sequence(VarParser.ident(), direction(), VarParser.ident(),
				(IdentExp left, Token tok, IdentExp right) -> new ViewMemberExp(left, tok, right));
	}
	
	public static Parser<ViewExp> viewMembers() {
		return Parsers.between(term("{"), member().many(), term("}")).
				map(new org.codehaus.jparsec.functors.Map<List<ViewMemberExp>, ViewExp>() {
					@Override
					public ViewExp map(List<ViewMemberExp> arg0) {
						return new ViewExp(arg0);
					}
				});
	}
	
	public static Parser<ViewExp> viewHdr() {
		return Parsers.sequence(VarParser.ident(), direction(), VarParser.ident(),
				(IdentExp varName, Token tok, IdentExp varType) -> new ViewExp(varName, tok, varType));
	}
	
	public static Parser<ViewExp> view03() {
		return Parsers.sequence(term("view"), viewHdr(), viewMembers(), VarParser.doEnd(),
				(Token tok, ViewExp view, ViewExp view2, Exp exp) -> new ViewExp(view, view2));
	}

	
}