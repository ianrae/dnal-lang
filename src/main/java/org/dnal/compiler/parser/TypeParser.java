package org.dnal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.codehaus.jparsec.functors.Tuple4;
import org.codehaus.jparsec.functors.Tuple5;
import org.dnal.compiler.parser.ast.EnumExp;
import org.dnal.compiler.parser.ast.EnumMemberExp;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullEnumTypeExp;
import org.dnal.compiler.parser.ast.FullListTypeExp;
import org.dnal.compiler.parser.ast.FullMapTypeExp;
import org.dnal.compiler.parser.ast.FullStructTypeExp;
import org.dnal.compiler.parser.ast.FullTypeExp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.RuleExp;
import org.dnal.compiler.parser.ast.StructExp;
import org.dnal.compiler.parser.ast.StructMemberExp;

public class TypeParser extends ParserBase {
    
	//rules have been moved to RuleParser

	//type x int > 0 end
	public static Parser<FullTypeExp> type01() {
		return Parsers.or(term("type")).next(Parsers.tuple(Parsers.INDEX, VarParser.ident(), VarParser.ident(), RuleParser.ruleMany(), VarParser.doEnd()))
				.map(new org.codehaus.jparsec.functors.Map<Tuple5<Integer, IdentExp, IdentExp, List<RuleExp>, Exp>, FullTypeExp>() {
					@Override
					public FullTypeExp map(Tuple5<Integer, IdentExp, IdentExp, List<RuleExp>, Exp> arg0) {
						return new FullTypeExp(arg0.a, arg0.b, arg0.c, arg0.d);
					}
				});
	}

	public static Parser<IdentExp> termStruct() {
		return term("struct").<IdentExp>retn(new IdentExp("struct"));
	}
    public static Parser<IdentExp> doStruct() {
        return Parsers.or(VarParser.ident(), termStruct());
    }

    public static Parser<Token> optionalOptionalArg() {
        return term("optional").optional();
    }
    public static Parser<Token> optionalUniqueArg() {
        return term("unique").optional();
    }
    
	public static Parser<StructMemberExp> structMembers00() {
		//Parsers.sequence(Parsers.INDEX, fooParser, Parsers.INDEX, LocationAnnotated::new);
		return Parsers.sequence(Parsers.INDEX, VarParser.ident(), Parsers.or(VarParser.ident(), listangle()), optionalOptionalArg(), optionalUniqueArg(),
				(Integer pos, IdentExp varName, IdentExp varType, Token opt, Token unique) -> new StructMemberExp(pos, varName, varType, opt, unique));
	}
	
	public static Parser<List<StructMemberExp>> structMembersMany() {
        return structMembers00().many().sepBy(term(","))
                .map(new org.codehaus.jparsec.functors.Map<List<List<StructMemberExp>>, List<StructMemberExp>>() {
                    @Override
                    public List<StructMemberExp> map(List<List<StructMemberExp>> arg0) {
						List<StructMemberExp> cc = new ArrayList<>();
						for(List<StructMemberExp> sub: arg0) {
							if (sub.size() > 1) {
								throw new IllegalArgumentException("Struct members must be separated by commas");
							}
							for(StructMemberExp re: sub) {
								cc.add(re);
							}
						}
						return cc;
                    }
                });    
	}
	

	public static Parser<StructExp> structMembers() {
		return Parsers.between(term("{"), structMembersMany(), term("}")).
				map(new org.codehaus.jparsec.functors.Map<List<StructMemberExp>, StructExp>() {
					@Override
					public StructExp map(List<StructMemberExp> arg0) {
						return new StructExp(arg0);
					}
				});
	}

	//type Colour struct { x int y int } end
	public static Parser<FullStructTypeExp> typestruct01() {
		return Parsers.sequence(term("type"), VarParser.ident(), doStruct(), structMembers(), RuleParser.ruleMany(),
				(Token tok, IdentExp varName, IdentExp struct, StructExp structMembers, List<RuleExp> rules) -> 
		new FullStructTypeExp(tok.index(), varName, struct, structMembers, rules)).followedBy(VarParser.doEnd());
	}
	
	//-----enum---
	public static Parser<IdentExp> doEnum() {
		return term("enum").<IdentExp>retn(new IdentExp("enum"));
	}

	public static Parser<EnumMemberExp> enumMembers00() {
		return Parsers.or(VarParser.ident()).
				map(new org.codehaus.jparsec.functors.Map<IdentExp, EnumMemberExp>() {
					@Override
					public EnumMemberExp map(IdentExp arg0) {
						return new EnumMemberExp(arg0, new IdentExp("string"));
					}
				});
	}
	
	public static Parser<List<EnumMemberExp>> enumMembersMany() {
        return enumMembers00().many().sepBy(term(","))
                .map(new org.codehaus.jparsec.functors.Map<List<List<EnumMemberExp>>, List<EnumMemberExp>>() {
                    @Override
                    public List<EnumMemberExp> map(List<List<EnumMemberExp>> arg0) {
						List<EnumMemberExp> cc = new ArrayList<>();
						for(List<EnumMemberExp> sub: arg0) {
							if (sub.size() > 1) {
								throw new IllegalArgumentException("Enum members must be separated by commas");
							}
							for(EnumMemberExp re: sub) {
								cc.add(re);
							}
						}
						return cc;
                    }
                });    
	}

	public static Parser<EnumExp> enumMembers() {
		return Parsers.between(term("{"), enumMembersMany(), term("}")).
				map(new org.codehaus.jparsec.functors.Map<List<EnumMemberExp>, EnumExp>() {
					@Override
					public EnumExp map(List<EnumMemberExp> arg0) {
						return new EnumExp(arg0);
					}
				});
	}

	//type Colour struct { x int y int } end
	public static Parser<FullEnumTypeExp> typeenum01() {
		return Parsers.sequence(term("type"), VarParser.ident(), doEnum(), enumMembers(), RuleParser.ruleMany(),
				(Token tok, IdentExp varName, IdentExp struct, EnumExp structMembers, List<RuleExp> rules) -> 
		new FullEnumTypeExp(tok.index(), varName, struct, structMembers, rules)).followedBy(VarParser.doEnd());
	}
	
	public static final Parser.Reference<IdentExp> listangleRef = Parser.newReference();
	public static Parser<IdentExp> listangleinner() {
		return Parsers.or(listangleRef.lazy(), VarParser.ident(), any());
	}
	public static Parser<IdentExp> listangle() {
		return Parsers.sequence(term("list"), term("<"), listangleinner(), term(">"),
				(Token tok1, Token tok2, IdentExp elementType, Token tok3) -> 
		new IdentExp(String.format("list<%s>", elementType.name())));
	}

	public static Parser<FullListTypeExp> typelist01() {
		return Parsers.sequence(term("type"), VarParser.ident(), 
				listangle(), RuleParser.ruleMany(),
				(Token tok, IdentExp varName, 
				 IdentExp elementType, List<RuleExp> rules) -> 
		new FullListTypeExp(tok.index(), varName, new IdentExp("list"), elementType, rules)).followedBy(VarParser.doEnd());
	}
	
	
	public static Parser<IdentExp> any() {
		return term("any").<IdentExp>retn(new IdentExp("any"));
	}

	public static final Parser.Reference<IdentExp> mapangleRef = Parser.newReference();
	public static Parser<IdentExp> mapangleinner() {
		return Parsers.or(mapangleRef.lazy(), VarParser.ident(), any());
	}
	public static Parser<IdentExp> mapangle() {
		return Parsers.sequence(term("map"), term("<"), mapangleinner(), term(">"),
				(Token tok1, Token tok2, IdentExp elementType, Token tok3) -> 
		new IdentExp(String.format("map<%s>", elementType.name())));
	}

	public static Parser<FullMapTypeExp> typemap01() {
		return Parsers.sequence(term("type"), VarParser.ident(), 
				mapangle(), RuleParser.ruleMany(),
				(Token tok, IdentExp varName, 
				 IdentExp elementType, List<RuleExp> rules) -> 
		new FullMapTypeExp(tok.index(), varName, new IdentExp("map"), elementType, rules)).followedBy(VarParser.doEnd());
	}
	
}