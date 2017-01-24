package org.dval;

public class ErrorMessage {
	private ErrorType errorType;
	private String message;
	private int lineNum;
	private String srcFile;
	private String statement;
	private String statementType;

	public ErrorMessage(ErrorType errorType, String message) {
		super();
		this.errorType = errorType;
		this.message = message;
	}
    public ErrorMessage(ErrorType errorType, String message, int lineNum) {
        super();
        this.errorType = errorType;
        this.message = message;
        this.lineNum = lineNum;
    }
    public ErrorMessage(int lineNum, String message) {
        super();
        this.errorType = ErrorType.PARSINGERROR;
        this.message = message;
        this.lineNum = lineNum;
    }

	public ErrorType getErrorType() {
		return errorType;
	}
	public String getMessage() {
		return message;
	}
    public int getLineNum() {
        return lineNum;
    }
    public String getSrcFile() {
        return srcFile;
    }
    public void setSrcFile(String srcFile) {
        this.srcFile = srcFile;
    }
    public String getStatement() {
        return statement;
    }
    public void setStatement(String statement) {
        this.statement = statement;
    }
    public String getStatementType() {
        return statementType;
    }
    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }
}