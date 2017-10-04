package org.dnal.compiler.parser;

import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullAssignmentExp;

public class FullParser {

	public static Parser<Exp> allStatements() {
		return Parsers.or(PackageParser.packageDecl(), PackageParser.importDecl(), 
		        VarParser.assignment00(), TypeParser.typestruct01(),
				TypeParser.typeenum01(), TypeParser.type01(), TypeParser.typelist01(), 
				RuleDeclParser.customRuleDecl(), ViewParser.view03());
//				followedBy(TerminalParser.token(";").optional());
	}
	public static Exp parse02(String input){
		VarParser.listmemberRef.set(VarParser.valueassign());		
		VarParser.structmemberRef.set(VarParser.valueassignstruct00());
		TypeParser.listangleRef.set(TypeParser.listangle());
		return FullParser.allStatements().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(input);
	}
	public static FullAssignmentExp parse01(String input){
		return (FullAssignmentExp) parse02(input);
	}

	public static List<Exp> fullParse(String input) {
		VarParser.listmemberRef.set(VarParser.valueassign());		
		VarParser.structmemberRef.set(VarParser.valueassignstruct00());
		TypeParser.listangleRef.set(TypeParser.listangle());
		return FullParser.allStatements().many().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse(input);
	}
}