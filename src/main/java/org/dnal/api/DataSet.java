package org.dnal.api;

import java.util.List;

import org.dnal.core.DType;
import org.dnal.core.DValue;


public interface DataSet {

    Transaction createTransaction();
    void registerBeanLoader(BeanLoader<?> loader);
    DValue getValue(String varName);
    <T> T getAsBean(String varName, Class<T> clazz);
    Generator createGeneratorEx();

    DataSet cloneDataSet();
    DataSet cloneEmptyDataSet();
    int size();
    List<String> getAllNames();
    DType getType(String typeName);
    List<DType> getTypes(TypeFilter filter);
}