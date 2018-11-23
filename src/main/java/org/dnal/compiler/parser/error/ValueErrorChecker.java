package org.dnal.compiler.parser.error;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.BooleanExp;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullAssignmentExp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.ListAssignExp;
import org.dnal.compiler.parser.ast.NumberExp;
import org.dnal.compiler.parser.ast.StringExp;
import org.dnal.compiler.parser.ast.StructAssignExp;

public class ValueErrorChecker extends ErrorCheckerBase {
	private List<FullAssignmentExp> typeL;

	private static final String NAME = "value";

	public ValueErrorChecker(DNALDocument doc, XErrorTracker et, LineLocator locator) {
		super(doc, et, locator);
		this.typeL = doc.getValues();
	}

	public void checkForErrors() {
		for(FullAssignmentExp exp : typeL) {
			checkValue(exp);
		}
	}

	//--
	private void checkValue(FullAssignmentExp valueExp) {
		IdentExp ident = valueExp.var;
		checkIdent(ident, NAME);
		seenTypesMap.put(ident.strValue(), "");

		ident = valueExp.type;
		if (valueExp.isListVar()) {
			IdentExp elementType = valueExp.getListSubType();
			checkType(elementType, NAME, valueExp.var.strValue());
		} else if (valueExp.isMapVar()) {
			IdentExp elementType = valueExp.getMapSubType();
			checkType(elementType, NAME, valueExp.var.strValue());
		} else {
			checkType(ident, NAME, valueExp.var.strValue());
		}
		checkAssignedValue(ident, valueExp.value, valueExp.var);
	}


	private void checkAssignedValue(IdentExp ident, Exp value, IdentExp variable) {
		if (doc.isTypeShape(ident, "int")) {
			if (!checkValueExp(value instanceof IntegerExp, value)) {
				addError2s("value '%s': can't assign '%s'. integer expected", variable.strValue(), value.strValue());
			}
		} else if (doc.isTypeShape(ident, "number")) {
			if (!checkValueExp(value instanceof NumberExp, value)) {
				addError2s("value '%s': can't assign '%s'. number expected", variable.strValue(), value.strValue());
			}
		} else if (doc.isTypeShape(ident, "boolean")) {
			if (!checkValueExp(value instanceof BooleanExp, value)) {
				addError2s("value '%s': can't assign '%s'. 'false' or 'true' expected", variable.strValue(), value.strValue());
			}
		} else if (doc.isTypeShape(ident, "string")) {
			if (!checkValueExp(value instanceof StringExp, value)) {
				addError2s("value '%s': can't assign '%s'. string expected", variable.strValue(), value.strValue());
			}
		} else if (doc.isTypeShape(ident, "list")) {
			if (!checkValueExp(value instanceof ListAssignExp, value)) {
				addError2s("value '%s': can't assign '%s'. list expected", variable.strValue(), value.strValue());
			}
		} else if (doc.isTypeShape(ident, "struct")) {
			if (!checkValueExp(value instanceof StructAssignExp, value)) {
				String s = value.getClass().getSimpleName();
				addError2s("value '%s': can't assign '%s'. struct expected", variable.strValue(), s);
			}
		}
	}

	private boolean checkValueExp(boolean isMatchingExplicitValue, Exp ident) {
		if (isMatchingExplicitValue || ident instanceof IdentExp) {
			return true;
		}
		return false;
	}
}