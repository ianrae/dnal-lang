package com.github.ianrae.dnalparse.parser.error;

import java.util.List;

import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.parser.DNALDocument;
import com.github.ianrae.dnalparse.parser.ast.Exp;

public class ParseErrorChecker {
	private DNALDocument doc;
//	private List<ErrorMessage> errL = new ArrayList<>();
	private TypeErrorChecker typeChecker;
	private ValueErrorChecker valueChecker;
	private ImportErrorChecker importChecker;
    private XErrorTracker et;

	public ParseErrorChecker(List<Exp> list, XErrorTracker et) {
		this.doc = new DNALDocument(list);
		this.et = et;
	}

	public boolean checkForErrors() {
	    int startCount = et.getErrorCount();
	    importChecker = new ImportErrorChecker(doc, et);
	    importChecker.checkForErrors();
        
		typeChecker = new TypeErrorChecker(doc, et);
		typeChecker.checkForErrors();

		valueChecker = new ValueErrorChecker(doc, et);
		valueChecker.setSeenTypes(typeChecker.seenTypes);
		valueChecker.checkForErrors();

		int endCount = et.getErrorCount();
		return (startCount == endCount);
	}


//	public void dump() {
//	    et.dumpErrors();
//?}

//	public int getNumErrors() {
//		return context.errL.size();
//	}
//	public List<ErrorMessage> getErrors() {
//		return context.errL;
//	}

	public DNALDocument getDoc() {
		return doc;
	}
}