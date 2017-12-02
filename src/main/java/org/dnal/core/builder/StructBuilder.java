package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.ErrorType;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.Shape;
import org.dnal.core.TypePair;
import org.dnal.core.xbuilder.XBuilderFactory;
import org.dnal.core.xbuilder.XDValueBuilder;
import org.dnal.core.xbuilder.XDateValueBuilder;
import org.dnal.core.xbuilder.XListValueBuilder;
import org.dnal.core.xbuilder.XStructValueBuilder;

public class StructBuilder extends Builder {
	private DStructType structType;
	private XBuilderFactory builderFactory;
	private XStructValueBuilder builder;

	public StructBuilder(DStructType type, List<NewErrorMessage> valErrorList) {
		super(valErrorList);
		this.builderFactory = new XBuilderFactory();
		this.structType = type;
		this.builder = new XStructValueBuilder(type);
	}

	public List<TypePair> getAllFields() {
		return builder.allFields;
	}

	public DValue getAlreadyBuiltField(String fieldName) {
		return builder.map.get(fieldName);
	}

	public void addField(String fieldName, DValue dval) {
		builder.addField(fieldName, dval, false);
	}
	public void addField(String fieldName, String input) {
		DType field = structType.getFields().get(fieldName);
		if (field == null) {
			builder.addParsingError("unknown field: " + fieldName);
		} else {
			DValue dval = build(field, input);
			builder.addField(fieldName, dval, false);
		}
	}

	public DValue finish() {
		wasSuccessful = builder.finish();
		valErrorList.addAll(builder.getValidationErrors());
		return builder.getDValue();
	}

	private DValue build(DType field, String input) {
		DValue dval = null; 
		
		Shape shape = field.getShape();
		switch(shape) {
		case INTEGER:
			dval = buildIntVal(field, input);
			break;
		case LONG:
			dval = buildLongVal(field, input);
			break;
		case NUMBER:
			dval = buildNumberVal(field, input);
			break;
		case BOOLEAN:
			dval = buildBooleanVal(field, input);
			break;
		case STRING:
			dval = buildStringVal(field, input);
			break;
		case DATE:
			dval = buildDateVal(field, input);
			break;
		case ENUM:
			dval = buildEnumVal(field, input);
			break;
		default:
			NewErrorMessage nem = new NewErrorMessage();
			nem.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
			nem.setFieldName("?");
			nem.setTypeName(field.getName());
			nem.setLineNum(0);
			nem.setMessage(String.format("uknown shape '%s'", field.getName()));
			nem.setErrorName(ErrorType.WRONGTYPE.name());
			this.valErrorList.add(nem);
			break;
		}
		
		return dval;
	}

	public void addOldErrMsgZ(ErrorType errType, String message) {
		NewErrorMessage nem = new NewErrorMessage();
		nem.setErrorType(NewErrorMessage.Type.VALIDATION_ERROR);
		nem.setFieldName("?");
		nem.setTypeName("?");
		nem.setLineNum(0);
		nem.setMessage("uknown shape");
		nem.setErrorName(ErrorType.WRONGTYPE.name());
		this.valErrorList.add(nem);
	}


	public void addField(String fieldName, String[] ar) {
		DType field = structType.getFields().get(fieldName);
		if (field == null) {
			builder.addParsingError("unknown field: " + fieldName);
		} else if (field instanceof DListType) {
			DListType listType = (DListType) field;
			XListValueBuilder lvb = new XListValueBuilder(listType);
			DType elType = listType.getElementType();
			for(String input: ar) {
				DValue dval = build(elType, input);
				lvb.addValue(dval);
			}
			if (!lvb.finish()) {
				valErrorList.addAll(lvb.getValidationErrors());
			} else {
				builder.addField(fieldName, lvb.getDValue(), false);
			}
		} else {
			addOldErrMsgZ(ErrorType.WRONGTYPE, "not a list!");

		}
	}

	protected DValue buildStringVal(DType field, String input) {
		DType type = field; //registry.getType(BuiltInTypes.STRING_SHAPE);
		XDValueBuilder builder = builderFactory.createBuilderFor(type);
		builder.buildFromString(input);
		if (! builder.finish()) {
			this.valErrorList.addAll(builder.getValidationErrors());
			return null;
		}
		return builder.getDValue();
	}
	protected DValue buildEnumVal(DType field, String input) {
		DType type = field; //registry.getType(BuiltInTypes.STRING_SHAPE);
		XDValueBuilder builder = builderFactory.createBuilderFor(type);
		builder.buildFromString(input);
		if (! builder.finish()) {
			this.valErrorList.addAll(builder.getValidationErrors());
			return null;
		}
		return builder.getDValue();
	}
	protected DValue buildIntVal(DType field, String input) {
		DType type = field; //registry.getType(BuiltInTypes.INTEGER_SHAPE);
		XDValueBuilder builder = builderFactory.createBuilderFor(type);
		builder.buildFromString(input);
		if (! builder.finish()) {
			this.valErrorList.addAll(builder.getValidationErrors());
			return null;
		}
		return builder.getDValue();
	}
	protected DValue buildLongVal(DType field, String input) {
		DType type = field; //registry.getType(BuiltInTypes.INTEGER_SHAPE);
		XDValueBuilder builder = builderFactory.createBuilderFor(type);
		builder.buildFromString(input);
		if (! builder.finish()) {
			this.valErrorList.addAll(builder.getValidationErrors());
			return null;
		}
		return builder.getDValue();
	}
	protected DValue buildNumberVal(DType field, String input) {
		DType type = field; //registry.getType(BuiltInTypes.NUMBER_SHAPE);
		XDValueBuilder builder = builderFactory.createBuilderFor(type);
		builder.buildFromString(input);
		if (! builder.finish()) {
			this.valErrorList.addAll(builder.getValidationErrors());
			return null;
		}
		return builder.getDValue();
	}
	protected DValue buildDateVal(DType field, String input) {
		DType type = field; //registry.getType(BuiltInTypes.DATE_SHAPE);
		XDateValueBuilder builder = (XDateValueBuilder) builderFactory.createBuilderFor(type);
//		builder.setDateFormat(dateFormat);
		builder.buildFromString(input);
		if (! builder.finish()) {
			this.valErrorList.addAll(builder.getValidationErrors());
			return null;
		}
		return builder.getDValue();
	}
	protected DValue buildBooleanVal(DType field, String input) {
		DType type = field; //registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
		XDValueBuilder builder = builderFactory.createBuilderFor(type);
		builder.buildFromString(input);
		if (! builder.finish()) {
			this.valErrorList.addAll(builder.getValidationErrors());
			return null;
		}
		return builder.getDValue();
	}

	public List<NewErrorMessage> getValidationErrors() {
		return valErrorList;
	}
}