package com.github.ianrae.dnalparse.parser.error;

import java.util.ArrayList;
import java.util.List;

import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.parser.DNALDocument;
import com.github.ianrae.dnalparse.parser.ast.IdentExp;

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