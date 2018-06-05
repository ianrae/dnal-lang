package org.dnal.compiler.et;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.error.ErrorInfo;
import org.dnal.compiler.parser.error.ErrorScope;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.logger.Log;

public class XErrorTracker {
    protected List<NewErrorMessage> errL = new ArrayList<>();
    protected Stack<ErrorScope> scopeStack = new Stack<>();
    protected Stack<ErrorInfo> errinfoStack = new Stack<>();
    public static boolean logErrors = false;
//    private String currentTypeName;
//    private String currentFieldName;
//    private String currentVarName;
//    private String currentActualValue;
//    private int currentListIndex = -1;
    
    public XErrorTracker() {
    	ErrorInfo errinfo = new ErrorInfo();
    	errinfoStack.push(errinfo);
    }

    public void pushScope(ErrorScope scope) {
        scopeStack.push(scope);
    }
    public void popScope() {
        scopeStack.pop();
    }
    public void pushErrorInfo(ErrorInfo errinfo) {
    	errinfoStack.push(errinfo);
    }
    public void popErrorInfo() {
    	errinfoStack.pop();
    }
    public ErrorInfo peekErrorInfo() {
    	return errinfoStack.peek();
    }
    
    public void clear() {
    	errL.clear();
    }
    
    /**
     * Transactions use this so they can keep all errors during commit in their
     * own error list.
     * Note. This approach only works in single-threaded use of dataset!
     * @param newL
     * @return
     */
    public List<NewErrorMessage> swapErrorList(List<NewErrorMessage> newL) {
    	List<NewErrorMessage> previous = errL;
    	errL = newL;
    	return previous;
    }
    
    public boolean areNoErrors() {
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
    	String srcFile = (err.getSrcFile() == null) ? "" : String.format("[%s]", err.getSrcFile());
    	String varName = (err.getVarName() == null) ? "?" : err.getVarName();
    	String fieldName = (err.getFieldName() == null) ? "" : "." + err.getFieldName();
    	String lineNum = (err.getLineNum() == 0) ? "" : "." + Integer.valueOf(err.getLineNum()).toString();
    	String actualValue = (err.getActualValue() == null) ? "" : String.format("(%s)", err.getActualValue());
    	String s = String.format("%s%s %s (%s) %s [%s%s] - %s %s",
    			srcFile, lineNum, 
    			err.getErrorType(), err.getErrorName(), varName,
    			err.getTypeName(), fieldName,
    			err.getMessage(),
    			actualValue);
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
        
        if (! errinfoStack.isEmpty()) {
        	ErrorInfo errinfo = errinfoStack.peek();
            if (errinfo.getCurrentTypeName() != null) {
            	err.setTypeName(errinfo.getCurrentTypeName());
            }
            if (errinfo.getCurrentFieldName() != null) {
            	err.setFieldName(errinfo.getCurrentFieldName());
            }
            if (errinfo.getCurrentVarName() != null) {
            	err.setVarName(errinfo.getCurrentVarName());
            }
            if (errinfo.getCurrentActualValue() != null) {
            	err.setActualValue(errinfo.getCurrentActualValue());
            }
            if (errinfo.getCurrentListIndex() >= 0) {
            	err.setListIndex(errinfo.getCurrentListIndex());
            }
        }
        
        
        this.errL.add(err);
        logIfEnabled(err);
    }
    public List<NewErrorMessage> getErrL() {
        return errL;
    }
	public void setCurrentTypeName(String currentTypeName) {
    	ErrorInfo errinfo = errinfoStack.peek();
		errinfo.setCurrentTypeName(currentTypeName);
	}
	public void setCurrentFieldName(String currentFieldName) {
    	ErrorInfo errinfo = errinfoStack.peek();
		errinfo.setCurrentFieldName(currentFieldName);
	}
	public void setCurrentVarName(String currentVarName) {
    	ErrorInfo errinfo = errinfoStack.peek();
		errinfo.setCurrentVarName(currentVarName);
	}
	public void setCurrentActualValue(String currentActualValue) {
    	ErrorInfo errinfo = errinfoStack.peek();
		errinfo.setCurrentActualValue(currentActualValue);
	}
	public void setCurrentListIndex(int index) {
    	ErrorInfo errinfo = errinfoStack.peek();
		errinfo.setCurrentListIndex(index);
	}
	public void propogateErrors(List<NewErrorMessage> errorList) {
		for(NewErrorMessage err: errorList) {
	        this.errL.add(err);
	        logIfEnabled(err);
		}
	}
	
	
	public void addParsingError(String message) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.PARSING_ERROR);
		nem.setMessage(message);
		addError(nem);
	}
	public void addAPIError(String message) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.API_ERROR);
		nem.setMessage(message);
		addError(nem);
	}
	public void addIOError(String message) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.IO_ERROR);
		nem.setMessage(message);
		addError(nem);
	}
	public void addValidationError(String message) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
		nem.setMessage(message);
		addError(nem);
	}


}