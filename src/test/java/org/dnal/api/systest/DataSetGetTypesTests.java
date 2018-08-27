package org.dnal.api.systest;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.dnal.api.DataSet;
import org.dnal.api.TypeFilter;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.junit.Test;

public class DataSetGetTypesTests extends SysTestBase {

    @Test
    public void testT6() {
        String src1 = "type Foo struct { x int, y int} end ";
        String src2 = "type Bar struct { a string, foos list<Foo> } end ";
        String src3 = "type Circle struct { bar Bar } end ";
//        String src4 = "let x Circle = { [ { 'abc', [ ] } ] }";
        String src4 = "let x Circle = {  { 'abc', [ ] }  }";
        chkValue("x", src1 + src2 + src3 + src4, 4, 1);

        String all = "INTEGER_SHAPE,LONG_SHAPE,NUMBER_SHAPE,STRING_SHAPE,BOOLEAN_SHAPE,DATE_SHAPE,ENUM_SHAPE,ANY_SHAPE,LIST_ANY_SHAPE,MAP_ANY_SHAPE,Foo,list<Foo>,Bar,Circle";
        chkTypes(TypeFilter.ALL, 14, all);
        
        String structs = "Foo,Bar,Circle";
        chkTypes(TypeFilter.STRUCT, 3, structs);
    }

	private void chkTypes(TypeFilter filter, int count, String expected) {
        DataSet ds = this.dataSetLoaded;
        List<DType> types = ds.getTypes(filter);
        assertEquals(count, types.size());
        StringJoiner joiner = new StringJoiner(",");
        for(DType dtype: types) {
        	joiner.add(dtype.getName());
        }
        log(joiner.toString());
        assertEquals(expected, joiner.toString());
	}
    
    
}
