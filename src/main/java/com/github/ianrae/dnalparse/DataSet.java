package com.github.ianrae.dnalparse;

import org.dnal.core.DValue;


public interface DataSet {

    Transaction createTransaction();
    void registerLoader(DValueLoader<?> loader);
    DValue getValue(String varName);
    <T> T getAsBean(String varName, Class<T> clazz);
    Generator createGenerator();

    DataSet cloneEmptyDataSet();
    int size();

}