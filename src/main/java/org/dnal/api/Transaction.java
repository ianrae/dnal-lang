package org.dnal.api;

import java.util.List;

import org.dnal.compiler.validate.ValidationOptions;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.DViewType;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.builder.BooleanBuilder;
import org.dnal.core.builder.DateBuilder;
import org.dnal.core.builder.EnumBuilder;
import org.dnal.core.builder.IntBuilder;
import org.dnal.core.builder.ListBuilder;
import org.dnal.core.builder.LongBuilder;
import org.dnal.core.builder.NumberBuilder;
import org.dnal.core.builder.StringBuilder;
import org.dnal.core.builder.StructBuilder;

public interface Transaction {
    
    void add(String name, DValue dval);
    boolean commit();
    List<NewErrorMessage> getValErrorList();
    DataSet getDataSet();
    ValidationOptions getValidationOptions();
    
    DValue createFromBean(Object bean) throws WorldException;
    
    //-------------- builder ------------------------
    DType getType(String typeName);
    DListType getListType(String typeName);
    DStructType getStructType(String typeName);
    DViewType getViewType(String viewName);
    
    StructBuilder createStructBuilder(DStructType structType);
    StructBuilder createStructBuilder(String typeName);
    
    IntBuilder createIntBuilder();
    IntBuilder createIntBuilder(String typeName);
    IntBuilder createIntBuilder(DType type);
   
    LongBuilder createLongBuilder();
    LongBuilder createLongBuilder(String typeName);
    LongBuilder createLongBuilder(DType type);
   
    BooleanBuilder createBooleanBuilder();
    BooleanBuilder createBooleanBuilder(String typeName);
    BooleanBuilder createBooleanBuilder(DType type);
   
    NumberBuilder createNumberBuilder();
    NumberBuilder createNumberBuilder(String typeName);
    NumberBuilder createNumberBuilder(DType type);
    
    DateBuilder createDateBuilder();
    DateBuilder createDateBuilder(String typeName);
    DateBuilder createDateBuilder(DType type);
    
    StringBuilder createStringBuilder();
    StringBuilder createStringBuilder(String typeName);
    StringBuilder createStringBuilder(DType type);
    
    EnumBuilder createEnumBuilder(String typeName);
    EnumBuilder createEnumBuilder(DType type);
    
    //for list builder the list elements should be built with lower-level DValueBuilder, assembled into the list
    //then added to the world all at once. DValueBuilders all should use same BuffereingWorldAdder
    ListBuilder createListBuilder(String typeName);
    ListBuilder createListBuilder(DListType type);
    
}
