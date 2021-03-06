package org.dnal.compiler.parser.error;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.NewErrorMessage;

public class ErrorTrackingBase {
	protected DNALDocument doc;
    private XErrorTracker et;
    private LineLocator lineLocator; //can be null
//	protected ErrorScopeStack scopeStack;
//	public static boolean logErrors = false;

	public ErrorTrackingBase(XErrorTracker et, LineLocator locator) {
		this.et = et;
		this.doc = null; //will be set later
		this.lineLocator = locator;
	}
	public ErrorTrackingBase(DNALDocument doc, XErrorTracker et, LineLocator locator) {
		this.et = et;
		this.doc = doc;
	}
	
	public void pushScope(ErrorScope scope) {
	    et.pushScope(scope);
	}
    public void popScope() {
        et.popScope();
    }
    public ErrorInfo getErrorInfo() {
    	return et.peekErrorInfo();
    }
	
	protected boolean areNoErrors() {
	    return et.getErrL().size() == 0;
	}
    protected boolean areSomeErrors() {
        return et.areErrors();
    }
	
	public void dumpErrors() {
	    et.dumpErrors();
	}
	public XErrorTracker getET() {
	    return et;
	}
	
	protected void addError(String fmt, Exp exp) {
		addError(null, fmt, exp);
	}
	protected void addError(Exp expParam, String fmt, Exp exp) {
		NewErrorMessage err = new NewErrorMessage();
		err.setMessage(String.format(fmt, exp.strValue()));
		this.setLineNum(err, expParam);
        addErrorObj(err);
	}
	protected void addError2(String fmt, String s, Exp exp) {
		NewErrorMessage err = new NewErrorMessage();
		err.setMessage(String.format(fmt, s, exp.strValue()));
        addErrorObj(err);
	}
	protected void addError3(String fmt, String s, String s2, Exp exp) {
		NewErrorMessage err = new NewErrorMessage();
		err.setMessage(String.format(fmt, s, s2, exp.strValue()));
        addErrorObj(err);
	}
	protected NewErrorMessage addError2s(String fmt, String s, String s2) {
		return addError2s(null, fmt, s, s2);
	}
	protected NewErrorMessage addError2s(Exp expParam, String fmt, String s, String s2) {
		NewErrorMessage err = new NewErrorMessage();
		err.setMessage(String.format(fmt, s, s2));
		setLineNum(err, expParam);
		addErrorObj(err);
		return err;
	}
    private void setLineNum(NewErrorMessage err, Exp expParam) {
		if (this.lineLocator != null && expParam != null) {
			int lineNum = lineLocator.findLineNumForPos(expParam.getPos());
			err.setLineNum(lineNum);
		} else {
			err.setLineNum(0); 
		}
	}
	protected void addError3s(String fmt, String s, String s2, String s3) {
		addError3s(null, fmt, s, s2, s3);
    }
	protected void addError3s(Exp expParam, String fmt, String s, String s2, String s3) {
		NewErrorMessage err = new NewErrorMessage();
		err.setMessage(String.format(fmt, s, s2, s3));
		this.setLineNum(err, expParam);
        addErrorObj(err);
    }
	
	protected void addErrorObj(NewErrorMessage err) {
	    this.et.addError(err);
	}
    public List<NewErrorMessage> getErrL() {
        return et.getErrL();
    }

    protected LineLocator getLineLocator() {
		return lineLocator;
	}
	public void setLineLocator(LineLocator lineLocator) {
		this.lineLocator = lineLocator;
	}
}