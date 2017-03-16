package org.dnal.core.oldbuilder;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.dnal.core.Shape;
import org.dnal.core.TypePair;

public class XStructValueBuilder extends XDValueBuilder {
	private DStructType structType;
    public Map<String,DValue> map = new TreeMap<>();
    public List<TypePair>  allFields;

	public XStructValueBuilder(DStructType type) {
		if (!type.isShape(Shape.STRUCT)) {
			addWrongTypeError("expecting struct");
			return;
		}
		this.type = type;
		this.structType = type;
		this.allFields = type.getAllFields();
	}

	public void buildFromString(String input) {
		//do nothing
	}
	public void addField(String fieldName, DValue dval) {
		addField(fieldName, dval, true);
	}
	public void addField(String fieldName, DValue dval, boolean logNullErr) {
		if (fieldName == null || fieldName.isEmpty()) {
			addNoDataError("null or empty fieldname");
			return;
		}
		
		boolean isOptional = structType.fieldIsOptional(fieldName);
		if (dval == null && !isOptional) {
			if (logNullErr) {
				addNoDataError("null field value");
			}
			return;
		}
		
		if (this.map.containsKey(fieldName)) {
			addDuplicateFieldError(String.format("already added field '%s'", fieldName));
			return;
		}
		else if (! isValidFieldName(fieldName)) {
			addUnknownFieldError(String.format("fieldName not allowed: '%s'", fieldName));
			return;
		}
		
		TypePair pair = fieldExists(fieldName);
		if (pair == null) {
			addUnknownFieldError(String.format("unknown field '%s'", fieldName));
			return;
		}
		
		DType target = pair.type; //structType.getFields().get(fieldName);
		if (!isOptional && ! target.isAssignmentCompatible(dval.getType())) {
			this.addWrongTypeError(String.format("field %s", fieldName)); //!!
		}
		
		map.put(fieldName, dval);
	}


	private TypePair fieldExists(String targetFieldName) {
        for(TypePair pair : allFields) {
            if (pair.name.equals(targetFieldName)) {
                return pair;
            }
        }
        return null;
    }

    private boolean isValidFieldName(String fieldName) {
		
		for(int i = 0; i < fieldName.length(); i++) {
			char ch = fieldName.charAt(i);
			if (Character.isWhitespace(ch)) {
				return false;
			} else if (Character.isISOControl(ch)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onFinish() {
		if (wasSuccessful()) {
			for(TypePair pair : allFields) {
			    String fieldName = pair.name;
				if (! map.containsKey(fieldName)) {
					addMissingFieldError(String.format("value for field '%s' not added to struct", fieldName));
				}
			}
			
			newDVal = new DValueImpl(type, map);
		}
	}
	
	
}