package org.dnal.compiler.generate;

/**
 * if isTopLevelValue is true then the value is a top-level value. eg let x Foo = ...
 *  and name is the top-level var name
 *  
 * otherwise the value is a member of a struct or map
 *  and name is the fieldName.
 * or the value is a member of a list and name is null 
 * @author ian
 *
 */
public class ValuePlacement {
	public boolean isTopLevelValue;
	public String name;
	
	public ValuePlacement(String varName, String name) {
		this.isTopLevelValue = (varName != null);
		this.name = (isTopLevelValue) ? varName : name;
	}
}
