package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.ErrorMessage;
import org.dnal.core.ErrorType;
import org.dnal.core.Shape;
import org.dnal.core.TypePair;
import org.dnal.core.oldbuilder.XBuilderFactory;
import org.dnal.core.oldbuilder.XDValueBuilder;
import org.dnal.core.oldbuilder.XDateValueBuilder;
import org.dnal.core.oldbuilder.XListValueBuilder;
import org.dnal.core.oldbuilder.XStructValueBuilder;

public class StructBuilder extends Builder {
    private DStructType structType;
    private XBuilderFactory builderFactory;
    private XStructValueBuilder builder;

    public StructBuilder(DStructType type, List<ErrorMessage> valErrorList) {
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
        builder.finish();
        valErrorList.addAll(builder.getValidationErrors());
        return builder.getDValue();
    }
    
    private DValue build(DType field, String input) {
        DValue dval = null; 
        if (field.isShape(Shape.INTEGER)) {
            dval = buildIntVal(field, input);
        } else if (field.isShape(Shape.LONG)) {
            dval = buildLongVal(field, input);
        } else if (field.isShape(Shape.NUMBER)) {
            dval = buildNumberVal(field, input);
        } else if (field.isShape(Shape.DATE)) {
            dval = buildDateVal(field, input);
        } else if (field.isShape(Shape.BOOLEAN)) {
            dval = buildBooleanVal(field, input);
        } else if (field.isShape(Shape.STRING)){
            dval = buildStringVal(field, input);
        } else {
            ErrorMessage err = new ErrorMessage(ErrorType.WRONGTYPE, "unknown shape!");
            this.valErrorList.add(err);
        }
        return dval;
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
            ErrorMessage err = new ErrorMessage(ErrorType.WRONGTYPE, "not a list!");
            this.valErrorList.add(err);
            
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
        builder.setDateFormat(dateFormat);
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

    public List<ErrorMessage> getValidationErrors() {
        return valErrorList;
    }
    public String getDateFormat() {
        return dateFormat;
    }
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}