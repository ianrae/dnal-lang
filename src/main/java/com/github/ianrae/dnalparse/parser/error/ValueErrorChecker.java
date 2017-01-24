package com.github.ianrae.dnalparse.parser.error;

import java.util.List;

import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.parser.DNALDocument;
import com.github.ianrae.dnalparse.parser.ast.BooleanExp;
import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.FullAssignmentExp;
import com.github.ianrae.dnalparse.parser.ast.IdentExp;
import com.github.ianrae.dnalparse.parser.ast.IntegerExp;
import com.github.ianrae.dnalparse.parser.ast.ListAssignExp;
import com.github.ianrae.dnalparse.parser.ast.NumberExp;
import com.github.ianrae.dnalparse.parser.ast.StringExp;
import com.github.ianrae.dnalparse.parser.ast.StructAssignExp;

public class ValueErrorChecker extends ErrorCheckerBase {
	private List<FullAssignmentExp> typeL;
	
	private static final String NAME = "value";

	public ValueErrorChecker(DNALDocument doc, XErrorTracker et) {
		super(doc, et);
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
		seenTypes.add(ident.strValue());

		ident = valueExp.type;
		  if (valueExp.isListVar()) {
	            IdentExp listElementType = valueExp.getListSubType();
                checkType(listElementType, NAME, valueExp.var.strValue());
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