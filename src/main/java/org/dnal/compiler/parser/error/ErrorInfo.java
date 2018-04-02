package org.dnal.compiler.parser.error;

public class ErrorInfo {
    private String currentTypeName;
    private String currentFieldName;
    private String currentVarName;
    private String currentActualValue;
    private int currentListIndex = -1;
    
	public String getCurrentTypeName() {
		return currentTypeName;
	}
	public void setCurrentTypeName(String currentTypeName) {
		this.currentTypeName = currentTypeName;
	}
	public String getCurrentFieldName() {
		return currentFieldName;
	}
	public void setCurrentFieldName(String currentFieldName) {
		this.currentFieldName = currentFieldName;
	}
	public String getCurrentVarName() {
		return currentVarName;
	}
	public void setCurrentVarName(String currentVarName) {
		this.currentVarName = currentVarName;
	}
	public String getCurrentActualValue() {
		return currentActualValue;
	}
	public void setCurrentActualValue(String currentActualValue) {
		this.currentActualValue = currentActualValue;
	}
	public int getCurrentListIndex() {
		return currentListIndex;
	}
	public void setCurrentListIndex(int currentListIndex) {
		this.currentListIndex = currentListIndex;
	}

}
