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
public class ScalarConvertUtil {
	
	public String toString(DValue dval) {
		return dval.asString();
	}
	public Object toObject(DValue dval) {
		return dval.getObject();
	}
	public Object toObject(DValue dval, Class<?> clazz) {
		Object obj = dval.getObject();
		if (clazz.isAssignableFrom(obj.getClass())) {
			return obj;
		}
		//can't convert. assume dval is not null
		switch(dval.getType().getShape()) {
		case INTEGER:
			if (clazz.equals(Short.class)) {
				Integer n = (Integer) dval.getObject();
//				  if ((i < Byte.MIN_VALUE) || (i > Byte.MAX_VALUE)) {
//				      throw new ArithmeticException("Value is out of range");
//				    }				
				return n.shortValue(); //narrowing. may lose info
			}
			break;
		case LONG:
			if (clazz.equals(Short.class)) {
				Long n = (Long) dval.getObject();
				return n.shortValue();
			}
			break;
		case NUMBER:
			if (clazz.equals(Short.class)) {
				Double n = (Double) dval.getObject();
				return n.shortValue();
			}
			break;
		case BOOLEAN:
			break;
		case STRING:
			break;
		case DATE:
			break;
		case LIST:
			break;
		case STRUCT:
			break;
		case ENUM:
			break;
		default:
			break;
		}
		
		return null;
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
	
	public DValue fromObject(Object obj, DType type, Transaction trans) {
		switch(type.getShape()) {
		case INTEGER:
			if (Integer.class.isAssignableFrom(obj.getClass())) {
				return trans.createIntBuilder(type).buildFrom((Integer) obj);
			}
			break;
		case LONG:
			if (Long.class.isAssignableFrom(obj.getClass())) {
				return trans.createLongBuilder(type).buildFrom((Long) obj);
			}
			break;
		case NUMBER:
			if (Double.class.isAssignableFrom(obj.getClass())) {
				return trans.createNumberBuilder(type).buildFrom((Double) obj);
			}
			break;
		case BOOLEAN:
			if (Boolean.class.isAssignableFrom(obj.getClass())) {
				return trans.createBooleanBuilder(type).buildFrom((Boolean) obj);
			}
			break;
		case STRING:
			if (String.class.isAssignableFrom(obj.getClass())) {
				return trans.createStringBuilder(type).buildFromString((String) obj);
			}
			break;
		case DATE:
			if (Date.class.isAssignableFrom(obj.getClass())) {
				return trans.createDateBuilder(type).buildFrom((Date) obj);
			}
			break;
		case LIST:
			break;
		case STRUCT:
			break;
		case ENUM:
			if (String.class.isAssignableFrom(obj.getClass())) {
				return trans.createEnumBuilder(type).buildFromString((String) obj);
			}
		default:
			break;
		}
		
		return null;
	}
}
