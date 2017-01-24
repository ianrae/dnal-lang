package com.github.ianrae.dnalparse.parser.error;


public class ErrorScope {
    public ErrorScope(String srcFile, String statement, String statementType) {
        super();
        this.srcFile = srcFile;
        this.statement = statement;
        this.statementType = statementType;
    }
    private String srcFile;
    private String statement;
    private String statementType;
    public String getSrcFile() {
        return srcFile;
    }
    public String getStatement() {
        return statement;
    }
    public String getStatementType() {
        return statementType;
    }

}
