package org.dnal.core;

public class NewErrorMessage {
	public enum Type {
		PARSING_ERROR,
		API_ERROR,
		IO_ERROR,
		VALIDATION_ERROR
	}
	
	private Type errorType;
	private String srcFile;
	private int lineNum;
	private String errorName;
	private String typeName;
	private String fieldName;
	private String varName;
	private String message;
	private String actualValue; //validation errors only
	private int listIndex;
	
	public NewErrorMessage() {
		errorType = Type.IO_ERROR;
	}
	
	public Type getErrorType() {
		return errorType;
	}
	public void setErrorType(Type errorType) {
		this.errorType = errorType;
	}
	public int getLineNum() {
		return lineNum;
	}
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	public String getSrcFile() {
		return srcFile;
	}
	public void setSrcFile(String srcFile) {
		this.srcFile = srcFile;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getErrorName() {
		return errorName;
	}
	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getActualValue() {
		return actualValue;
	}

	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}

	public int getListIndex() {
		return listIndex;
	}

	public void setListIndex(int listIndex) {
		this.listIndex = listIndex;
	}

}
