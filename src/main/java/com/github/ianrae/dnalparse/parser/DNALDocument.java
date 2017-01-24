package com.github.ianrae.dnalparse.parser;

import java.util.ArrayList;
import java.util.List;

import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.FullAssignmentExp;
import com.github.ianrae.dnalparse.parser.ast.FullTypeExp;
import com.github.ianrae.dnalparse.parser.ast.IdentExp;
import com.github.ianrae.dnalparse.parser.ast.ImportExp;
import com.github.ianrae.dnalparse.parser.ast.PackageExp;
import com.github.ianrae.dnalparse.parser.ast.RuleDeclExp;

public class DNALDocument {
	private List<Exp> statementList;

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
		List<FullTypeExp> typeL = new ArrayList<>();
		for (Exp exp : statementList) {
			if (isTypeExp(exp)) {
				typeL.add((FullTypeExp) exp);
			}
		}
		return typeL;
	}
	public List<FullAssignmentExp> getValues() {
		List<FullAssignmentExp> valueL = new ArrayList<>();
		for (Exp exp : statementList) {
			if (exp instanceof FullAssignmentExp) {
				valueL.add((FullAssignmentExp) exp);
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
		for(FullTypeExp type : this.getTypes()) {
			if (type.var.strValue().equals(typeName)) {
				return type;
			}
		}
		return null;
	}
	public FullAssignmentExp findValue(String varName) {
		for(FullAssignmentExp type : this.getValues()) {
			if (type.var.strValue().equals(varName)) {
				return type;
			}
		}
		return null;
	}
    public List<Exp> getStatementList() {
        return statementList;
    }
}