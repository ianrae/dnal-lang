package org.dnal.api.beancopier;

import java.math.BigDecimal;
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
			break;
		default:
			break;
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
		}
		return ok;
	}

	private boolean isByte(Number num, Class<?> clazz) {
		if (clazz.equals(Byte.class)) {
			int n = num.intValue();
			if ((n < Byte.MIN_VALUE) || (n > Byte.MAX_VALUE)) {
				return false;
			}				
			return true;
		}
		return false;
	}
	private boolean isShort(Number num, Class<?> clazz) {
		if (clazz.equals(Short.class)) {
			int n = num.intValue();
			if ((n < Short.MIN_VALUE) || (n > Short.MAX_VALUE)) {
				return false;
			}				
			return true;
		}
		return false;
	}
	private boolean isLong(Number num, Class<?> clazz) {
		if (clazz.equals(Long.class)) {
			int n = num.intValue();
			if ((n < Long.MIN_VALUE) || (n > Long.MAX_VALUE)) {
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
}
