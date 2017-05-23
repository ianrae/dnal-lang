package org.dnal.api.bean;

import java.util.Date;
import java.util.List;

import org.dnal.api.BeanLoader;
import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.impl.CompilerContext;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.ErrorType;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.TypePair;
import org.dnal.core.builder.ListBuilder;
import org.dnal.core.builder.StructBuilder;
import org.dnal.core.repository.World;

public class ReflectionBeanLoader implements BeanLoader<Object> {

	private Transaction trans;
	private DataSet ds;
	private String typeName; //type or view
	private BeanMethodBuilder beanMethodBuilder;
    private XErrorTracker et;
    private String currentFieldName; //for logging only
    private FieldConverter fieldConverter;

	public ReflectionBeanLoader(String typeName, DataSet ds, XErrorTracker et, FieldConverter fieldConverter) {
		this.typeName = typeName;
		this.ds = ds;
		this.et = et;
		this.beanMethodBuilder = new BeanMethodBuilder(et);
		this.fieldConverter = fieldConverter;
	}

	@Override
	public Class<?> willLoad() {
		return Object.class; //not used
	}

	@Override
	public Object create(DValue dval) {
		return null; //not supported
	}
	
	private void addError(String msg) {
		if (currentFieldName != null) {
			msg = currentFieldName + ": " + msg;
		}
		NewErrorMessage errMsg = new NewErrorMessage();
		errMsg.setErrorName(ErrorType.WRONGTYPE.name());
		errMsg.setMessage(msg);
		et.addError(errMsg);
	}

	private void buildMethodCacheIfNeeded(Object bean, String typeNameParam) {
		DStructType dtype = getTypeName(typeNameParam);
		beanMethodBuilder.buildMethodCacheIfNeeded(bean, dtype);
	}
	private DStructType getTypeName(String typeNameParam) {
		DStructType dtype = trans.getStructType(typeNameParam);
		if (dtype == null) {
			dtype = trans.getViewType(typeNameParam);
		}
		return dtype;
	}

	@Override
	public DValue createDValue(Object bean) {
		this.trans = ds.createTransaction();
		return doCreateDValue(bean, typeName);
	}
	
	private DValue doCreateDValue(Object bean, String typeNameParam) {
		buildMethodCacheIfNeeded(bean, typeNameParam);
		DStructType dtype = getTypeName(typeNameParam);
		StructBuilder builder = trans.createStructBuilder(dtype);
		List<TypePair> allFields = dtype.getAllFields();
		for(TypePair pair: allFields) {
//			log("zz " + pair.name);
			currentFieldName = pair.name;
			DValue inner = buildFieldValue(trans, pair, bean);
			if (inner != null || dtype.fieldIsOptional(pair.name)) {
				builder.addField(pair.name, inner);
			}
		}

		DValue dval = builder.finish();
		if (! builder.wasSuccessful()) {
	        for(NewErrorMessage err: trans.getValErrorList()) {
	            et.addError(err);
	        }
			
			et.dumpErrors();
			return null;
		}
		
//		//validate
//		trans.add("anyname", dval);
//		if (! trans.commit()) {
//			et.dumpErrors();
//			return null;
//		}
		
		return dval;
	}

	private DValue buildFieldValue(Transaction trans, TypePair pair, Object bean) {
		Object res = getFromBean(bean, pair.name, pair.type);
		if (res == null) {
			return null;
		}
		DValue dval = convertToDVal(trans, pair.type, res);
		return dval;
	}
	
	private DValue convertToDVal(Transaction trans, DType type, Object res) {
		if (fieldConverter != null) {
			return fieldConverter.convertToDVal(trans, type, res);
		}
		
		DValue dval = null;
		
		switch(type.getShape()) {
		case INTEGER:
			dval = trans.createIntBuilder().buildFrom(getAsInt(res));
			break;
		case LONG:
			dval = trans.createLongBuilder().buildFrom(getAsLong(res));
			break;
		case NUMBER:
			dval = trans.createNumberBuilder().buildFrom(getAsDouble(res));
			break;
		case BOOLEAN:
			dval = trans.createBooleanBuilder().buildFrom(getAsBoolean(res));
			break;
		case STRING:
			dval = trans.createStringBuilder().buildFromString(res.toString());
			break;
		case DATE:
			dval = trans.createDateBuilder().buildFrom(getAsDate(res));
			break;
		case LIST:
			dval = buildList(trans, type, res);
			break;
		case STRUCT:
			dval = buildStruct(trans, type, res);
			break;
		case ENUM:
			dval = trans.createEnumBuilder(type).buildFromString(res.toString());
			break;
		default:
			addError(String.format("type %s, unknown shape %s", type.getName(), type.getShape()));
			break;
		}
		return dval;
	}

	private DValue buildList(Transaction trans, DType type, Object res) {
		if (! (type instanceof DListType)) {
			addError(String.format("type %s not a list", type.getName()));
			return null;
		}
		
		DListType dtype = (DListType) type;
		ListBuilder builder = trans.createListBuilder(dtype);
		
		if (res instanceof List) {
			List<?> inputL = (List<?>) res;
			for(Object element: inputL) {
				DValue inner = this.convertToDVal(trans, dtype.getElementType(), element);
				builder.addElement(inner);
			}
		}
		
		return builder.finish();
	}
	private DValue buildStruct(Transaction trans, DType type, Object res) {
		if (! (type instanceof DStructType)) {
			addError(String.format("type %s not a struct", type.getName()));
			return null;
		}
		
		DValue dval = doCreateDValue(res, type.getName());
		return dval;
	}


	private void addWrongTypeError(String expectedType, Object res) {
		String typename = (res == null) ? "?" : res.getClass().getName();
		addError(String.format("wrong type: got '%s' but expected integer", typename));
	}
	private Integer getAsInt(Object res) {
		if (res instanceof Number) {
			Number num = (Number) res;
			return num.intValue();
		} else {
			addWrongTypeError("int", res);
		}
		return null;
	}
	private Long getAsLong(Object res) {
		if (res instanceof Number) {
			Number num = (Number) res;
			return num.longValue();
		} else {
			addWrongTypeError("long", res);
		}
		return null;
	}
	private Double getAsDouble(Object res) {
		if (res instanceof Number) {
			Number num = (Number) res;
			return num.doubleValue();
		} else {
			addWrongTypeError("double", res);
		}
		return null;
	}
	private Boolean getAsBoolean(Object res) {
		if (res instanceof Boolean) {
			Boolean bb = (Boolean) res;
			return bb;
		} else {
			addWrongTypeError("boolean", res);
		}
		return null;
	}
	private Date getAsDate(Object res) {
		if (res instanceof Date) {
			Date bb = (Date) res;
			return bb;
		} else {
			addWrongTypeError("date", res);
		}
		return null;
	}

	private Object getFromBean(Object bean, String fieldName, DType type)  {
		return beanMethodBuilder.getFromBean(bean, fieldName, type);
	}


	@Override
	public void attach(DTypeRegistry registry, World world, CompilerContext context) {
	}

}