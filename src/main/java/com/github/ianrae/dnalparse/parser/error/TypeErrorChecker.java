package com.github.ianrae.dnalparse.parser.error;

import java.util.List;

import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.parser.DNALDocument;
import com.github.ianrae.dnalparse.parser.ast.FullTypeExp;
import com.github.ianrae.dnalparse.parser.ast.IdentExp;

public class TypeErrorChecker extends ErrorCheckerBase {
	private List<FullTypeExp> typeL;

	public TypeErrorChecker(DNALDocument doc, XErrorTracker et) {
		super(doc, et);
		this.typeL = doc.getTypes();
	}

	public void checkForErrors() {
		for(FullTypeExp exp : typeL) {
			checkType(exp);
		}
	}

	//--
	private void checkType(FullTypeExp typeExp) {
		IdentExp ident = typeExp.var;
		checkIdent(ident, "type");
		seenTypes.add(ident.strValue());

		ident = typeExp.type;
		checkType(ident, "type", typeExp.var.strValue());
	}
}