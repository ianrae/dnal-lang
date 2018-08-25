package org.dnal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Token;
import org.codehaus.jparsec.functors.Tuple4;
import org.dnal.compiler.parser.ast.BasicExp;
import org.dnal.compiler.parser.ast.BooleanExp;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullAssignmentExp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.ListAssignExp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.compiler.parser.ast.MapAssignExp;
import org.dnal.compiler.parser.ast.NumberExp;
import org.dnal.compiler.parser.ast.StringExp;
import org.dnal.compiler.parser.ast.StructAssignExp;
import org.dnal.compiler.parser.ast.StructMemberAssignExp;
import org.dnal.compiler.parser.ast.ViaExp;

public class VarParser {
    
	public static Parser<Exp> eqassign() {
		return TerminalParser.tokenExp("=", new BasicExp("="));
	}

//	public static Parser<IntegerExp> negintegervalueassign() {
//		return Parsers.sequence(TerminalParser.token("-"), TerminalParser.numberSyntacticParser).
//				map(new org.codehaus.jparsec.functors.Map<String, IntegerExp>() {
//					@Override
//					public IntegerExp map(String arg0) {
//						return new IntegerExp(-1 * Integer.parseInt(arg0));
//					}
//				});
//	}
	
	public static Exp numberBuilder(Token negSign, String input) {
	    if (input != null && input.contains(".")) {
	        NumberExp exp = new NumberExp(Double.parseDouble(input));
	        if (negSign != null) {
	            exp.val = -1.0 * exp.val;
	        }
	        return exp;
	    } else {
	        Long lvalue = Long.parseLong(input);
	        Integer ivalue = lvalue.intValue();
	        if (ivalue.longValue() == lvalue.longValue()) {
	            IntegerExp exp = new IntegerExp(lvalue.intValue());
	            if (negSign != null) {
	                exp.val = -1 * exp.val;
	            }
	            return exp;
	        } else {
                LongExp exp = new LongExp(lvalue);
                if (negSign != null) {
                    exp.val = -1 * exp.val;
                }
                return exp;
	        }
	    }
	}
	
    public static Parser<Token> optionalNegSign() {
        return term("-").optional();
    }
	
//    public static Parser<String> x55() {
//        return Parsers.or(TerminalParser.numberSyntacticParser, TerminalParser.integerSyntacticParser);
//    }    
    
    public static Parser<Exp> someNumberValueassign() {
        return Parsers.sequence(optionalNegSign(), TerminalParser.numberSyntacticParser, (Token tok, String s) -> numberBuilder(tok, s));
    }
	
//	public static Parser<IntegerExp> integervalueassign() {
//		return Parsers.or(TerminalParser.integerSyntacticParser).
//				map(new org.codehaus.jparsec.functors.Map<String, IntegerExp>() {
//					@Override
//					public IntegerExp map(String arg0) {
//						return new IntegerExp(Integer.parseInt(arg0));
//					}
//				});
//	}
//    public static Parser<NumberExp> numbervalueassign() {
//        return Parsers.or(TerminalParser.numberSyntacticParser).
//                map(new org.codehaus.jparsec.functors.Map<String, NumberExp>() {
//                    @Override
//                    public NumberExp map(String arg0) {
//                        return new NumberExp(Double.parseDouble(arg0));
//                    }
//                });
//    }
//    public static Parser<NumberExp> negnumbervalueassign() {
//        return Parsers.sequence(TerminalParser.token("-"), TerminalParser.numberSyntacticParser).
//                map(new org.codehaus.jparsec.functors.Map<String, NumberExp>() {
//                    @Override
//                    public NumberExp map(String arg0) {
//                        return new NumberExp(-1 * Double.parseDouble(arg0));
//                    }
//                });
//    }
	public static Parser<StringExp> stringvalueassign() {
		return Parsers.or(TerminalParser.stringSyntacticParser).
				map(new org.codehaus.jparsec.functors.Map<String, StringExp>() {
					@Override
					public StringExp map(String arg0) {
						return new StringExp(arg0);
					}
				});
	}

	public static Parser<BooleanExp> booleanvalueassign() {
		return Parsers.or(
				TerminalParser.tokenExpT("true" ,new BooleanExp(true)),
				TerminalParser.tokenExpT("false", new BooleanExp(false)));
	}

	public static final Parser.Reference<Exp> listmemberRef = Parser.newReference();
	public static Parser<ListAssignExp> listvalueassign() {
		return Parsers.between(term("["), listmemberRef.lazy().many().sepBy(term(",")), term("]")).
				map(new org.codehaus.jparsec.functors.Map<List<List<Exp>>, ListAssignExp>() {
					@Override
					public ListAssignExp map(List<List<Exp>> arg0) {
						List<Exp> list = new ArrayList<>();
						if (! arg0.isEmpty()) {
							for(List<Exp> sublist : arg0) {
								if (! sublist.isEmpty()) {
									list.add(sublist.get(0));
								}
							}
						}
						return new ListAssignExp(list);
					}
				});
	}

	
	public static final Parser.Reference<Exp> structmemberRef = Parser.newReference();
	public static Parser<StructAssignExp> structvalueassign() {
		return Parsers.between(term("{"), 
				structmemberRef.lazy().many().sepBy(term(",")), term("}")).
				map(new org.codehaus.jparsec.functors.Map<List<List<Exp>>, StructAssignExp>() {
					@Override
					public StructAssignExp map(List<List<Exp>> arg0) {
						List<Exp> list = new ArrayList<>();
						for(List<Exp> sublist : arg0) {
							if (! sublist.isEmpty()) {
								list.add(sublist.get(0));
							}
						}
						return new StructAssignExp(list);
					}
				});
	}
	
	public static Parser<Exp> identOrString() {
		return Parsers.or(
		        ident(),
		        stringvalueassign());
	}
	public static IdentExp asIdentExp(Exp exp) {
		if (exp instanceof StringExp) {
			StringExp sexp = (StringExp) exp;
			return new IdentExp(sexp.val);
		} else {
			return (IdentExp) exp;
		}
	}
    public static Parser<Exp> struct_someNumberValueAssign() {
        return Parsers.sequence(identOrString(), term(":"), VarParser.someNumberValueassign(),
                (Exp exp1, Token tok, Exp exp2) -> new StructMemberAssignExp(asIdentExp(exp1), exp2));
    }
	public static Parser<Exp> struct_stringvalueassign() {
		return Parsers.sequence(identOrString(), term(":"), VarParser.stringvalueassign(),
				(Exp exp1, Token tok, Exp exp2) -> new StructMemberAssignExp(asIdentExp(exp1), exp2));
	}
	public static Parser<Exp> struct_booleanvalueassign() {
		return Parsers.sequence(identOrString(), term(":"), VarParser.booleanvalueassign(),
				(Exp exp1, Token tok, Exp exp2) -> new StructMemberAssignExp(asIdentExp(exp1), exp2));
	}
	public static Parser<Exp> struct_varname() {
		return Parsers.sequence(identOrString(), term(":"), ident(),
				(Exp exp1, Token tok, Exp exp2) -> new StructMemberAssignExp(asIdentExp(exp1), exp2));
	}
    public static Parser<Exp> struct_null() {
        return Parsers.sequence(identOrString(), term(":"), termNull(),
                (Exp exp1, Token tok, Exp exp2) -> new StructMemberAssignExp(asIdentExp(exp1), exp2));
    }
	public static Parser<Exp> struct_listvalueassign() {
		return Parsers.sequence(identOrString(), term(":"), VarParser.listvalueassign(),
				(Exp exp1, Token tok, Exp exp2) -> new StructMemberAssignExp(asIdentExp(exp1), exp2));
	}
	public static Parser<Exp> struct_structvalueassign() {
		return Parsers.sequence(identOrString(), term(":"), VarParser.structvalueassign(),
				(Exp exp1, Token tok, Exp exp2) -> new StructMemberAssignExp(asIdentExp(exp1), exp2));
	}

	public static Parser<Exp> valueassigninstruct() {
		return Parsers.or(
		        struct_someNumberValueAssign(),
				struct_stringvalueassign(),
				struct_booleanvalueassign(), 
				struct_varname(),
				struct_null(),
				struct_listvalueassign(),
				struct_structvalueassign());
	}
	
    public static Parser<IdentExp> termNull() {
        return term("null").<IdentExp>retn(new IdentExp("null"));
    }

	public static Parser<Exp> valueassignstruct00() {
		return Parsers.or(VarParser.valueassigninstruct(), termNull(), valueassign());
	}
	//end struct

    private static Parser<Exp> zvalueassign() {
        return Parsers.or(
                VarParser.someNumberValueassign(),
                VarParser.stringvalueassign(),
                VarParser.booleanvalueassign(), 
                ident(),
                VarParser.listvalueassign(),
                VarParser.structvalueassign());
    }
	
	
	public static Parser<Exp> valueassign() {
		return Parsers.or(
		        viaDecl(),
		        zvalueassign());
	}
	
    public static Parser<ViaExp> viaDecl() {
        return Parsers.sequence(VarParser.ident().optional(), term("via"), VarParser.ident(), zvalueassign(),
                (IdentExp exp, Token tok, IdentExp field, Exp val)
                -> new ViaExp(exp, field, val));
    }    
    
    
	

	public static Parser<Exp> assignmentUnused() {
		return Parsers.sequence(term("let"), TerminalParser.identSyntacticParser,
				(Token tok, String arg0) -> new IdentExp(arg0));
	}
	public static Parser<IdentExp> ident() {
		return Parsers.or(TerminalParser.identSyntacticParser).
				map(new org.codehaus.jparsec.functors.Map<String, IdentExp>() {
					@Override
					public IdentExp map(String arg0) {
						return new IdentExp(arg0);
					}
				});
	}
	public static Parser<Exp> assignmentUnused0() {
		return Parsers.sequence(term("let"), ident(), ident(),
				(Token tok, IdentExp varName, IdentExp varType) -> new FullAssignmentExp(tok.index(), varName, varType, null));
	}
	
	public static Parser<IdentExp> listangle() {
		return Parsers.sequence(term("list"), term("<"), Parsers.or(any(), VarParser.ident()), term(">"),
				(Token tok1, Token tok2, IdentExp elementType, Token tok3) -> 
		new IdentExp(String.format("list<%s>", elementType.name())));
	}
	public static Parser<IdentExp> mapangle() {
		return Parsers.sequence(term("map"), term("<"), Parsers.or(any(), VarParser.ident()), term(">"),
				(Token tok1, Token tok2, IdentExp elementType, Token tok3) -> 
		new IdentExp(String.format("map<%s>", elementType.name())));
	}
	
	public static Parser<IdentExp> any() {
		return term("any").<IdentExp>retn(new IdentExp("any"));
	}
	
	public static Parser<IdentExp> typeOrListType() {
		return Parsers.or(listangle(), mapangle(), ident());
	}
	
	//let x list<string> = [ ]
	//let x ColourList = [ ]
	
	//let x int = 5
	public static Parser<Exp> assignment000() {
		return Parsers.or(term("let")).next(Parsers.tuple(ident(), typeOrListType(), VarParser.eqassign(), valueassign()))
				.map(new org.codehaus.jparsec.functors.Map<Tuple4<IdentExp, IdentExp, Exp, Exp>, FullAssignmentExp>() {
					@Override
					public FullAssignmentExp map(Tuple4<IdentExp, IdentExp, Exp, Exp> arg0) {
						Exp dd = arg0.d;

						return new FullAssignmentExp(0, arg0.a, arg0.b, dd);
					}
				});
	}

	public static Parser<Exp> assignment00() {
//		return Parsers.or(assignment000(), assignment001());
		return assignment000();
	}

	public static Parser<Token> term(String name) {
		return TerminalParser.token(name);
	}	

	public static Parser<Exp> doEnd() {
		return term("end").<Exp>retn(new BasicExp("end"));
	}
}