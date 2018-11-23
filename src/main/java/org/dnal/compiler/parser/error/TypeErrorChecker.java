package org.dnal.compiler.parser.error;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.FullTypeExp;
import org.dnal.compiler.parser.ast.IdentExp;

public class TypeErrorChecker extends ErrorCheckerBase {
	private List<FullTypeExp> typeL;

	public TypeErrorChecker(DNALDocument doc, XErrorTracker et, LineLocator locator) {
		super(doc, et, locator);
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
		seenTypesMap.put(ident.strValue(), "");

		ident = typeExp.type;
		checkType(ident, "type", typeExp.var.strValue());
	}
}