package com.github.ianrae.dnalparse.parser.error;

import java.util.List;

import org.dnal.core.ErrorMessage;
import org.dnal.core.logger.Log;

import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.parser.DNALDocument;
import com.github.ianrae.dnalparse.parser.ast.Exp;

public class ErrorTrackingBase {
//	protected List<ErrorMessage> errL;
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
		ErrorMessage err = new ErrorMessage(0, String.format(fmt, exp.strValue()));
        addErrorObj(err);
	}
	protected void addError2(String fmt, String s, Exp exp) {
		ErrorMessage err = new ErrorMessage(0, String.format(fmt, s, exp.strValue()));
        addErrorObj(err);
	}
	protected void addError3(String fmt, String s, String s2, Exp exp) {
		ErrorMessage err = new ErrorMessage(0, String.format(fmt, s, s2, exp.strValue()));
        addErrorObj(err);
	}
	protected void addError2s(String fmt, String s, String s2) {
		ErrorMessage err = new ErrorMessage(0, String.format(fmt, s, s2));
		addErrorObj(err);
	}
    protected void addError3s(String fmt, String s, String s2, String s3) {
        ErrorMessage err = new ErrorMessage(0, String.format(fmt, s, s2, s3));
        addErrorObj(err);
    }
	
	protected void addErrorObj(ErrorMessage err) {
	    this.et.addError(err);
	}
    public List<ErrorMessage> getErrL() {
        return et.getErrL();
    }


}