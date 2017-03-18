package org.dnal.api;

import java.util.List;

import org.dnal.core.DValue;


public interface DataSet {

    Transaction createTransaction();
    void registerBeanLoader(BeanLoader<?> loader);
    DValue getValue(String varName);
    <T> T getAsBean(String varName, Class<T> clazz);
    Generator createGenerator();

    DataSet cloneEmptyDataSet();
    int size();
    List<String> getAllNames();

}