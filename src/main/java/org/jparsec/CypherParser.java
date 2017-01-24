package org.jparsec;

import org.codehaus.jparsec.Indentation;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;

import static org.codehaus.jparsec.Scanners.isChar;

import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.pattern.CharPredicates;

import java.util.List;

public class CypherParser {

	private static final Terminals KEYWORDS = Terminals.operators("type","end","optional");
	private static final Terminals PRIMTYPES = Terminals.operators("string","int");
	private static final Parser<?> WHITESPACE = Scanners.WHITESPACES;
	  private static final Parser<Void> COMMENT = Scanners.lineComment("#");
	  static final Indentation INDENTATION = new Indentation();

	private static final Parser<Identifier> IDENTIFIER = isChar(CharPredicates.IS_ALPHA_)
			.followedBy(Scanners.many(CharPredicates.IS_ALPHA_NUMERIC_))
			.source().map(new Map<String, Identifier>() {
				@Override
				public Identifier map(String s) {
					return new Identifier(s);
				}
			});

	private static final Parser.Reference<Function> functionsRef = Parser.newReference();

	private static final Parser<List<Expr>> parameter =
			Parsers.or(functionsRef.lazy(), IDENTIFIER).sepBy(isChar(','));

	private static final Parser<Function> functions =
			Parsers.sequence(IDENTIFIER, parameter.between(isChar('('), isChar(')')),
					new Map2<Identifier, List<Expr>, Function>() {

				@Override
				public Function map(Identifier identifier, List<Expr> expr) {
					return new Function(identifier, expr);
				}
			});

	private static final Parser<DNType> dtypex =
			Parsers.sequence(IDENTIFIER, parameter.between(isChar('{'), isChar('}')),
					new Map2<Identifier, List<Expr>, DNType>() {

				@Override
				public DNType map(Identifier identifier, List<Expr> expr) {
					return new DNType(identifier, expr);
				}
			});

	
	private static final Parser<DNType> dtype =
			Parsers.between(KEYWORDS.token("type").followedBy(WHITESPACE), dtypex, KEYWORDS.token("end"));
	
	public Expr parse(CharSequence input) {
		functionsRef.set(functions);
//		return Parsers.or(functions, IDENTIFIER, dtype).parse(input);
		return Parsers.or(dtype, IDENTIFIER).parse(input);
	}

//	 static Expr zparse(Parser<Expr> parser, String source) {
//		    return parser.from(Indentation.WHITESPACES.or(COMMENT).many()))
//		        .parse(source);
//	 }	
}
