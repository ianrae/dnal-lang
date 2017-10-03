package org.dnal.compiler.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.dnal.compiler.parser.ast.Exp;

public class RuleParser extends ParserBase {
    
	public static Parser<Exp> ruleOperand() {
		return Parsers.or(VarParser.someNumberValueassign(), strArg(), VarParser.ident());
	}
	
	
}