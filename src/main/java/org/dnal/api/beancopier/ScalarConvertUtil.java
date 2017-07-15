package org.dnal.api.beancopier;

import java.math.BigDecimal;
import java.util.Date;

import org.dnal.api.Transaction;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;

/**
 * Common code for converting to and from scalar values.
 * @author ian
 *
 */
public class ScalarConvertUtil {
	private XErrorTracker et;

	public ScalarConvertUtil(XErrorTracker errorTracker) {
		this.et = errorTracker;
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
			Integer nInt = (Integer) dval.getObject();
			if (clazz.equals(int.class)) {
				return nInt;
			} else if (isShort(nInt, clazz)) {
				return nInt.shortValue(); //narrowing. 
			} else if (isByte(nInt, clazz)) {
				return nInt.byteValue(); //narrowing. 
			} else if (isLong(nInt, clazz)) {
				return Long.valueOf(nInt.longValue());
			}
			break;
		case LONG:
			Long nLong = (Long) dval.getObject();
			if (clazz.equals(long.class)) {
				return nLong;
			} else if (isShort(nLong, clazz)) {
				return nLong.shortValue(); //narrowing. 
			} else if (isByte(nLong, clazz)) {
				return nLong.byteValue();
			}
			break;
		case NUMBER:
			Double nDouble = (Double) dval.getObject();
			if (clazz.equals(double.class)) {
				return nDouble;
			}
			
			if (isFractional(nDouble)) {
				if (isFloat(nDouble.floatValue(), clazz)) {
					return nDouble.floatValue();
				}
			} else {
				if (isShort(nDouble.intValue(), clazz)) {
					return nDouble.shortValue(); //narrowing. 
				} else if (isByte(nDouble.intValue(), clazz)) {
					return nDouble.byteValue();
				} else if (isLong(nDouble.longValue(), clazz)) {
					return Long.valueOf(nDouble.longValue());
				}
			}
			break;
		case BOOLEAN:
			Boolean bool = (Boolean) dval.getObject();
			if (clazz.equals(boolean.class)) {
				return bool;
			}
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
			if (! clazz.isEnum()) {
				addEnumError(clazz);
			} else {
				return convertToEnum(dval, clazz);
			}
			break;
		default:
			break;
		}

		return null;
	}

	private Object convertToEnum(DValue dval, Class<?> clazz) {
		String strval = dval.asString();
		
		for(int i = 0; i < clazz.getEnumConstants().length; i++) {
			Object x = clazz.getEnumConstants()[i];
			if (strval.equals(x.toString())) {
				return x;
			}
		}
		
		return null;
	}
	private boolean isFractional(Double dd) {
		BigDecimal bd = new BigDecimal(dd);
		boolean ok = false;
		try {
			bd.intValueExact();
			ok = true;
		} catch (ArithmeticException e) {
			ok = false;
		}
		return !ok;
	}

	private boolean isByte(Number num, Class<?> clazz) {
		if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
			int n = num.intValue();
			if ((n < Byte.MIN_VALUE) || (n > Byte.MAX_VALUE)) {
				addRangeError(n, clazz);
				return false;
			}				
			return true;
		}
		return false;
	}
	private boolean isShort(Number num, Class<?> clazz) {
		if (clazz.equals(Short.class) || clazz.equals(short.class)) {
			int n = num.intValue();
			if ((n < Short.MIN_VALUE) || (n > Short.MAX_VALUE)) {
				addRangeError(n, clazz);
				return false;
			}				
			return true;
		}
		return false;
	}
	private boolean isLong(Number num, Class<?> clazz) {
		if (clazz.equals(Long.class) || clazz.equals(long.class)) {
			int n = num.intValue();
			if ((n < Long.MIN_VALUE) || (n > Long.MAX_VALUE)) {
				addRangeError(n, clazz);
				return false;
			}				
			return true;
		}
		return false;
	}
	private boolean isFloat(Number num, Class<?> clazz) {
		if (clazz.equals(Float.class) || clazz.equals(float.class)) {
			int n = num.intValue();
			if ((n < Float.MIN_VALUE) || (n > Float.MAX_VALUE)) {
				addRangeError(n, clazz);
				return false;
			}				
			return true;
		}
		return false;
	}
	
	//========= from conversion ===========
	//TODO: much more to do here

	public DValue fromObject(Object obj, DType type, Transaction trans) {
		final Class<?> clazz = obj.getClass();
		
		switch(type.getShape()) {
		case INTEGER:
			if (Integer.class.isAssignableFrom(clazz)) {
				return trans.createIntBuilder(type).buildFrom((Integer) obj);
			} else if (Byte.class.equals(clazz)) {
				Byte bb = (Byte) obj;
				return trans.createIntBuilder(type).buildFrom(bb.intValue());
			} else if (Short.class.equals(clazz)) {
				Short bb = (Short) obj;
				return trans.createIntBuilder(type).buildFrom(bb.intValue());
			}
			break;
		case LONG:
			if (Long.class.isAssignableFrom(obj.getClass())) {
				return trans.createLongBuilder(type).buildFrom((Long) obj);
			} else if (Byte.class.equals(clazz)) {
				Byte bb = (Byte) obj;
				return trans.createLongBuilder(type).buildFrom(bb.longValue());
			} else if (Short.class.equals(clazz)) {
				Short bb = (Short) obj;
				return trans.createLongBuilder(type).buildFrom(bb.longValue());
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
	
	private void addRangeError(long val, Class<?> clazz) {
		et.addParsingError(String.format("value %d is out of range for '%s'", val, clazz.getSimpleName()));
	}
	private void addEnumError(Class<?> clazz) {
		et.addParsingError(String.format("class '%s' is not an enum", clazz.getSimpleName()));
	}
}
