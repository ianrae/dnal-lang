package org.dnal.compiler.parser.error;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.ViewExp;
import org.dnal.core.logger.Log;

public class ViewErrorChecker extends ErrorCheckerBase {
	private static final String NAME = "view";
	
	private List<ViewExp> viewL;

	public ViewErrorChecker(DNALDocument doc, XErrorTracker et) {
		super(doc, et);
		this.viewL = doc.getViews();
	}

	public void checkForErrors() {
		for(ViewExp exp : viewL) {
			Log.log(exp.typeName + "vvvvvvvvv");
			checkView(exp);
		}
	}

	//--
	private void checkView(ViewExp viewExp) {
		IdentExp ident = viewExp.viewName;
		checkIdent(ident, NAME);
		seenTypes.add(ident.strValue());
		checkViewType(ident, "view", viewExp.typeName.val);
	}
}