package org.dnal.compiler.parser.error;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.Exp;

public class ParseErrorChecker {
	private DNALDocument doc;
	private TypeErrorChecker typeChecker;
	private ValueErrorChecker valueChecker;
	private ImportErrorChecker importChecker;
    private XErrorTracker et;
    private LineLocator lineLocator;

	public ParseErrorChecker(List<Exp> list, XErrorTracker et, LineLocator locator) {
		this.doc = new DNALDocument(list);
		this.et = et;
		this.lineLocator = locator;
	}

	public boolean checkForErrors() {
	    int startCount = et.getErrorCount();
	    importChecker = new ImportErrorChecker(doc, et, lineLocator);
	    importChecker.checkForErrors();
        
		typeChecker = new TypeErrorChecker(doc, et, lineLocator);
		typeChecker.checkForErrors();

		valueChecker = new ValueErrorChecker(doc, et, lineLocator);
		valueChecker.setSeenTypes(typeChecker.seenTypes);
		valueChecker.checkForErrors();
		
		int endCount = et.getErrorCount();
		return (startCount == endCount);
	}


	public DNALDocument getDoc() {
		return doc;
	}
}