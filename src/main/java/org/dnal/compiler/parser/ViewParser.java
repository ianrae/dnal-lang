package org.dnal.compiler.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.ViewExp;

public class ViewParser extends ParserBase {
    

	public static Parser<ViewExp> view01() {
		return Parsers.sequence(VarParser.ident(), term("->"), VarParser.ident(),
				(IdentExp varName, Token tok, IdentExp varType) -> new ViewExp(varName, tok, varType));
	}
	
	public static Parser<ViewExp> view03() {
		return Parsers.sequence(term("view"), view01(), VarParser.doEnd(),
				(Token tok, ViewExp view, Exp exp) -> view);
	}

	
}