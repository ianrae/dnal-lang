package org.dnal.compiler.parser.error;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.IdentExp;

public class ErrorCheckerBase extends ErrorTrackingBase {
	protected List<String> seenTypes = new ArrayList<>();

	public ErrorCheckerBase(DNALDocument doc, XErrorTracker et) {
		super(doc, et);
	}

    protected void checkIdent(IdentExp identExp, String elName) {
		if (isPrimitiveType(identExp)) {
			addError2("%s name '%s' can't be a primitive type", elName, identExp);
		}

		if (isAlreadyDefinedType(identExp)) {
			addError2("%s name '%s' has already been defined", elName, identExp);
		}
	}
	protected void checkType(IdentExp identExp, String elName, String typeName) {
		if (! isPrimitiveType(identExp) && ! isAlreadyDefinedType(identExp)) {
			addError3("%s '%s' has unknown type '%s'", elName, typeName, identExp);
		}
	}
	protected void checkViewType(IdentExp identExp, String elName, IdentExp typeName) {
		if (! isAlreadyDefinedType(typeName)) {
			addError3("%s '%s' has unknown type '%s'", elName, identExp.val, typeName);
		}
	}

	private boolean isAlreadyDefinedType(IdentExp typeNameExp) {
		if (seenTypes.contains(typeNameExp.strValue())) {
			return true;
		}
		seenTypes.add(typeNameExp.strValue());
		return false;
	}

	private boolean isPrimitiveType(IdentExp ident) {
		return TypeInfo.isPrimitiveType(ident);
	}

	public void setSeenTypes(List<String> seenTypes) {
		this.seenTypes = seenTypes;
	}

}