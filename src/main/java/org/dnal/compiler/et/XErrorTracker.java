package org.dnal.compiler.et;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.error.ErrorScope;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.logger.Log;

public class XErrorTracker {
    protected List<NewErrorMessage> errL = new ArrayList<>();
    protected Stack<ErrorScope> scopeStack = new Stack<>();
    public static boolean logErrors = false;
    private String currentTypeName;
    private String currentFieldName;

    public void pushScope(ErrorScope scope) {
        scopeStack.push(scope);
    }
    public void popScope() {
        scopeStack.pop();
    }
    
    public void clear() {
    	errL.clear();
    }
    
    protected boolean areNoErrors() {
        return errL.size() == 0;
    }
    
    public void dumpErrors() {
        for(NewErrorMessage err : errL) {
            Log.log(errToString(err));
        }
    }
    
    private void logIfEnabled(NewErrorMessage err) {
        if (logErrors) {
            Log.log(errToString(err));
        }
    }
    
    public String errToString(NewErrorMessage err) {
    	String s = String.format("[%s].%d %s (%s) [%s.%s] - %s", err.getSrcFile(), err.getLineNum(), 
    			err.getErrorType(), err.getErrorName(), err.getTypeName(), err.getFieldName(),
    			err.getMessage());
    	return s;
    }

    protected void addError(String fmt, Exp exp) {
    	NewErrorMessage err = new NewErrorMessage();
    	err.setMessage(String.format(fmt, exp.strValue()));
        addError(err);
    }
    protected void addError2(String fmt, String s, Exp exp) {
    	NewErrorMessage err = new NewErrorMessage();
        err.setMessage(String.format(fmt, s, exp.strValue()));
        addError(err);
    }
    protected void addError3(String fmt, String s, String s2, Exp exp) {
    	NewErrorMessage err = new NewErrorMessage();
        err.setMessage(String.format(fmt, s, s2, exp.strValue()));
        addError(err);
    }
    protected void addError2s(String fmt, String s, String s2) {
    	NewErrorMessage err = new NewErrorMessage();
    	err.setMessage(String.format(fmt, s, s2));
        addError(err);
    }
    protected void addError3s(String fmt, String s, String s2, String s3) {
    	NewErrorMessage err = new NewErrorMessage();
        err.setMessage(String.format(fmt, s, s2, s3));
        addError(err);
    }
    
    public int getErrorCount() {
        return errL.size();
    }
    public boolean areErrors() {
        return errL.size() != 0;
    }
    
    public void addError(NewErrorMessage err) {
        if (! scopeStack.isEmpty()) {
            ErrorScope scope = scopeStack.peek();
            err.setSrcFile(scope.getSrcFile());
        }
        
        if (this.currentTypeName != null) {
        	err.setTypeName(currentTypeName);
        }
        if (this.currentFieldName != null) {
        	err.setFieldName(currentFieldName);
        }
        
        this.errL.add(err);
        logIfEnabled(err);
    }
    public List<NewErrorMessage> getErrL() {
        return errL;
    }
	public void setCurrentTypeName(String currentTypeName) {
		this.currentTypeName = currentTypeName;
	}
	public void setCurrentFieldName(String currentFieldName) {
		this.currentFieldName = currentFieldName;
	}


}