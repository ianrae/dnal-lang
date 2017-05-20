package org.dnal.api.bean;

import java.util.Date;

import org.dnal.api.Transaction;
import org.dnal.core.DType;
import org.dnal.core.DValue;

/**
 * Common code for converting to and from scalar values.
 * @author ian
 *
 */
public class ScalarStringConvert {
	
	public String toString(DValue dval) {
		return dval.asString();
	}
	
	//========= from conversion ===========
	public DValue fromString(String input, DType type, Transaction trans) {
		switch(type.getShape()) {
		case INTEGER:
			return trans.createIntBuilder(type).buildFromString(input);
		case LONG:
			return trans.createLongBuilder(type).buildFromString(input);
		case NUMBER:
			return trans.createNumberBuilder(type).buildFromString(input);
		case BOOLEAN:
			return trans.createBooleanBuilder(type).buildFromString(input);
		case STRING:
			return trans.createStringBuilder(type).buildFromString(input);
		case DATE:
			return trans.createDateBuilder(type).buildFromString(input);
		case LIST:
			break;
		case STRUCT:
			break;
		case ENUM:
			return trans.createEnumBuilder(type).buildFromString(input);
		default:
			break;
		}
		
		return null;
	}
	
}
