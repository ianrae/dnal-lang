package org.dnal.compiler.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullAssignmentExp;
import org.dnal.compiler.parser.ast.FullTypeExp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.ImportExp;
import org.dnal.compiler.parser.ast.PackageExp;
import org.dnal.compiler.parser.ast.RuleDeclExp;

public class DNALDocument {
	private List<Exp> statementList;
	private Map<String,FullTypeExp> typesMap = null;
	private Map<String,FullAssignmentExp> valuesMap = null;
	
	public DNALDocument(List<Exp> list) {
		this.statementList = list;
	}
	
	public PackageExp getPackage() {
	    for(Exp exp: statementList) {
	        if (exp instanceof PackageExp) {
	            return (PackageExp) exp;
	        }
	    }
	    return null;
	}
	
	//--
    public List<ImportExp> getImports() {
        List<ImportExp> list = new ArrayList<>();
        for (Exp exp : statementList) {
            if (exp instanceof ImportExp) {
                list.add((ImportExp) exp);
            }
        }
        return list;
    }
    public List<RuleDeclExp> getRuleDeclarations() {
        List<RuleDeclExp> declL = new ArrayList<>();
        for (Exp exp : statementList) {
            if (exp instanceof RuleDeclExp) {
                declL.add((RuleDeclExp) exp);
            }
        }
        return declL;
    }
	public List<FullTypeExp> getTypes() {
		boolean buildMap = false;
		if (typesMap == null) {
			typesMap = new HashMap<>();
			buildMap = true;
		}
		List<FullTypeExp> typeL = new ArrayList<>();
		for (Exp exp : statementList) {
			if (isTypeExp(exp)) {
				FullTypeExp ftexp = (FullTypeExp) exp;
				typeL.add(ftexp);
				if (buildMap) {
					String typeName = ftexp.var.strValue();
					typesMap.put(typeName, ftexp);
				}
			}
		}
		return typeL;
	}
	public List<FullAssignmentExp> getValues() {
		boolean buildMap = false;
		if (valuesMap == null) {
			valuesMap = new HashMap<>();
			buildMap = true;
		}
		
		List<FullAssignmentExp> valueL = new ArrayList<>();
		for (Exp exp : statementList) {
			if (exp instanceof FullAssignmentExp) {
				FullAssignmentExp fae = (FullAssignmentExp) exp;
				valueL.add(fae);
				if (buildMap) {
					String varName = fae.var.strValue();
					valuesMap.put(varName, fae);
				}
			}
		}
		return valueL;
	}
	private boolean isTypeExp(Exp exp) {
		return exp instanceof FullTypeExp;
	}
	
	public boolean isTypeShape(IdentExp ident, String shape) {
		if (ident.strValue().equals(shape)) {
			return true;
		}
		
		String current = ident.strValue();
		//!!handle runaway 
		while(current !=  null) {
			FullTypeExp type = findType(current);
			if (type == null) {
				return false;
			}
			
			if (type.type.strValue().equals(shape)) {
				return true;
			}
			current = type.type.strValue();
		}
		
		return false;
	}
	public String getShape(IdentExp ident) {
		
		String current = ident.strValue();
		//!!handle runaway 
		while(current !=  null) {
			FullTypeExp type = findType(current);
			if (type == null) {
				return current;
			}
			
			current = type.type.strValue();
		}
		
		return "unknowntype??";
	}
	
	public FullTypeExp findType(String typeName) {
		if (typesMap == null) {
			this.getTypes();
		}
		return typesMap.get(typeName);
//		for(FullTypeExp type : this.getTypes()) {
//			if (type.var.strValue().equals(typeName)) {
//				return type;
//			}
//		}
//		return null;
	}
	public FullAssignmentExp findValue(String varName) {
		if (valuesMap == null) {
			this.getValues();
		}
		return valuesMap.get(varName);
//		for(FullAssignmentExp type : this.getValues()) {
//			if (type.var.strValue().equals(varName)) {
//				return type;
//			}
//		}
//		return null;
	}
    public List<Exp> getStatementList() {
        return statementList;
    }
}