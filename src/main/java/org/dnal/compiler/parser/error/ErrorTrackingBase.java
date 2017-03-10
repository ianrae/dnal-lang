package org.dnal.compiler.parser.error;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.NewErrorMessage;

public class ErrorTrackingBase {
	protected DNALDocument doc;
    private XErrorTracker et;
//	protected ErrorScopeStack scopeStack;
//	public static boolean logErrors = false;

	public ErrorTrackingBase(XErrorTracker et) {
		this.et = et;
		this.doc = null; //will be set later
	}
	public ErrorTrackingBase(DNALDocument doc, XErrorTracker et) {
		this.et = et;
		this.doc = doc;
	}
	
	public void pushScope(ErrorScope scope) {
	    et.pushScope(scope);
	}
    public void popScope() {
        et.popScope();
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
		NewErrorMessage err = new NewErrorMessage();
		err.setMessage(String.format(fmt, exp.strValue()));
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
	protected void addError2s(String fmt, String s, String s2) {
		NewErrorMessage err = new NewErrorMessage();
		err.setMessage(String.format(fmt, s, s2));
		addErrorObj(err);
	}
    protected void addError3s(String fmt, String s, String s2, String s3) {
		NewErrorMessage err = new NewErrorMessage();
		err.setMessage(String.format(fmt, s, s2, s3));
        addErrorObj(err);
    }
	
	protected void addErrorObj(NewErrorMessage err) {
	    this.et.addError(err);
	}
    public List<NewErrorMessage> getErrL() {
        return et.getErrL();
    }


}