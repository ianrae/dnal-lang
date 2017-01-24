package org.dnal.core.builder;

import java.util.List;

import org.dnal.core.BuiltInTypes;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.ErrorMessage;

/**
 * Builds top-level values
 * 
 * @author ian
 *
 */
public class BuilderFactory {

    private DTypeRegistry registry;
    private List<ErrorMessage> valErrorList;

    public BuilderFactory(DTypeRegistry registry, List<ErrorMessage> valErrorList) {
        this.registry = registry;
        this.valErrorList = valErrorList;
    }
    
    public DStructType getStructType(String typeName) {
        DStructType structType = (DStructType) registry.getType(typeName);
        return structType;
    }
    public StructBuilder createStructBuilder(DStructType structType) {
        StructBuilder builder = new StructBuilder(structType, valErrorList);
        return builder;
    }
    public StructBuilder createStructBuilder(String typeName) {
        DStructType structType = getStructType(typeName);
        StructBuilder builder = new StructBuilder(structType, valErrorList);
        return builder;
    }
    
    public IntBuilder createIntegerBuilder() {
        DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
        return createIntegerBuilder(type);
    }
    public IntBuilder createIntegerBuilder(String typeName) {
        DType type = registry.getType(typeName);
        return createIntegerBuilder(type);
    }
    public IntBuilder createIntegerBuilder(DType type) {
        IntBuilder builder = new IntBuilder(type, valErrorList);
        return builder;
    }
    
    public LongBuilder createLongBuilder() {
        DType type = registry.getType(BuiltInTypes.LONG_SHAPE);
        return createLongBuilder(type);
    }
    public LongBuilder createLongBuilder(String typeName) {
        DType type = registry.getType(typeName);
        return createLongBuilder(type);
    }
    public LongBuilder createLongBuilder(DType type) {
        LongBuilder builder = new LongBuilder(type, valErrorList);
        return builder;
    }
    
   
    public BooleanBuilder createBooleanBuilder() {
        DType type = registry.getType(BuiltInTypes.BOOLEAN_SHAPE);
        return createBooleanBuilder(type);
    }
    public BooleanBuilder createBooleanBuilder(String typeName) {
        DType type = registry.getType(typeName);
        return createBooleanBuilder(type);
    }
    public BooleanBuilder createBooleanBuilder(DType type) {
        BooleanBuilder builder = new BooleanBuilder(type, valErrorList);
        return builder;
    }
   
    public NumberBuilder createNumberBuilder() {
        DType type = registry.getType(BuiltInTypes.NUMBER_SHAPE);
        return createNumberBuilder(type);
    }
    public NumberBuilder createNumberBuilder(String typeName) {
        DType type = registry.getType(typeName);
        return createNumberBuilder(type);
    }
    public NumberBuilder createNumberBuilder(DType type) {
        NumberBuilder builder = new NumberBuilder(type, valErrorList);
        return builder;
    }
    
    public DateBuilder createDateBuilder() {
        DType type = registry.getType(BuiltInTypes.DATE_SHAPE);
        return createDateBuilder(type);
    }
    public DateBuilder createDateBuilder(String typeName) {
        DType type = registry.getType(typeName);
        return createDateBuilder(type);
    }
    public DateBuilder createDateBuilder(DType type) {
        DateBuilder builder = new DateBuilder(type, valErrorList);
        return builder;
    }
    
    public StringBuilder createStringBuilder() {
        DType type = registry.getType(BuiltInTypes.STRING_SHAPE);
        return createStringBuilder(type);
    }
    public StringBuilder createStringBuilder(String typeName) {
        DType type = registry.getType(typeName);
        return createStringBuilder(type);
    }
    public StringBuilder createStringBuilder(DType type) {
        StringBuilder builder = new StringBuilder(type, valErrorList);
        return builder;
    }
    
//    public ZEnumBuilder createEnumBuilder() {
//        DType type = registry.getType(BuiltInTypes.INTEGER_SHAPE);
//        return createEnumBuilder(type);
//    }
    public EnumBuilder createEnumBuilder(String typeName) {
        DType type = registry.getType(typeName);
        return createEnumBuilder(type);
    }
    public EnumBuilder createEnumBuilder(DType type) {
        EnumBuilder builder = new EnumBuilder(type, valErrorList);
        return builder;
    }
    
    //for list builder the list elements should be built with lower-level DValueBuilder, assembled into the list
    //then added to the world all at once. DValueBuilders all should use same BuffereingWorldAdder
    public ListBuilder createListBuilder(String typeName) {
        DListType type = (DListType) registry.getType(typeName);
        return createListBuilder(type);
    }
    public ListBuilder createListBuilder(DListType type) {
        ListBuilder builder = new ListBuilder(type, valErrorList);
        return builder;
    }
    
}
