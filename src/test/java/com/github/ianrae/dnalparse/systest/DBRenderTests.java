package com.github.ianrae.dnalparse.systest;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dnal.core.DListType;
import org.dnal.core.DStructHelper;
import org.dnal.core.DValue;
import org.dnal.core.DValueImpl;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ianrae.dnalparse.DataSet;

public class DBRenderTests extends SysTestBase {

    public static class DiffList {
        public String a;
        public List<Object> L;
    }

    public static class DBRendr {
        private StringBuilder sb = new StringBuilder();
        private ObjectMapper mapper = new ObjectMapper(); //maybe static for per!!??
        private String currentFieldName;
//        private Stack<String> fieldNameStack = new Stack<>();
        private int missingRefCount;
        private Stack<Map<String,Object>> mapStack = new Stack<>();

        public String render(DValue dval) {
            resetFieldName();
//            fieldNameStack.push(currentFieldName);
            Map<String,Object> map = new HashMap<>();
            mapStack.push(map);
            doRender(map, dval, 0);
            renderObjMapper(map);
            mapStack.pop();
            return sb.toString();
        }
        private void resetFieldName() {
            currentFieldName = "_";
        }
        private void doRender(Map<String, Object> map, DValue dval, int depth) {

            switch(dval.getType().getShape()) {
            case INTEGER:
            case LONG:
            case BOOLEAN:
            case STRING:
            case DATE:
            case ENUM:
                renderField(map, dval.getObject());
                break;
            case LIST:
                renderList(map, dval, dval.asList(), depth);
                break;
            case STRUCT:
                renderStruct(map, dval, dval.asStruct(), depth);
                break;
            default:
                break;
            }
        }

        //are pers ids long or string??
        private void renderReference(Map<String, Object> map, DValue dval, String fieldName) {
            Long id = genRefId(dval);
            String sav = currentFieldName;
            currentFieldName = genCarrierFieldName(fieldName);
            renderField(map, id);
            currentFieldName = sav; //restore
        }
        private String genCarrierFieldName(String fieldName) {
            return "_id:" + fieldName;                
        }
        private Long genRefId(DValue dval) {
            Long id = (Long) dval.getPersistenceId();
            if (id == null) {
                missingRefCount++;
                id = 0L;
            }
            return id;
        }
        private boolean hasRefId(DValue dval) {
            return (dval.getPersistenceId() != null);
        }
        private void renderStruct(Map<String, Object> map, DValue dval, DStructHelper helper, int depth) {
            for(String fieldName: helper.getFieldNames()) {
                DValue inner = helper.getField(fieldName);
                String sav = currentFieldName;
                currentFieldName = fieldName;
                if (depth >= 0 && inner.getType().isStructShape()) {
                    if (hasRefId(inner)) {
                        renderReference(map, inner, fieldName);
                    } else {
                        Map<String,Object> map2 = new HashMap<>();
                        mapStack.push(map2);
                        doRender(map2, inner, depth + 1); //**recursion**
                        mapStack.pop();
                        renderField(map, map2);
                    }
                } else {
                    doRender(map, inner, depth + 1);  //**recursion**
                }
                currentFieldName = sav;
            }
        }

        //     * { "_": [ {"a": "I1:5", "L": [ 15, 16 ]}  ] }
        private void renderList(Map<String, Object> map, DValue listDVal, List<DValue> list, int depth) {
            List<DiffList> diffList = new ArrayList<>();
            DiffList diff = new DiffList();
            diffList.add(diff);
            diff.a = "I1:5";
            DListType listType = (DListType) listDVal.getType();
            diff.L = new ArrayList<>();
            if (listType.getElementType().isScalarShape()) {
                for(DValue inner: list) {
                    diff.L.add(inner.getObject());
                }
            } else if (listType.getElementType().isStructShape()) {
                for(DValue inner: list) {
                    if (hasRefId(inner)) {
                        doRender(map, inner, depth + 1); //**recursion**
                    } else {
                        Long id = genRefId(inner);
                        diff.L.add(id);
                    }
                }
            }
             renderField(map, diffList);
        }

        private void renderField(Map<String,Object> map, Object object) {
            map.put(currentFieldName, object);
        }

        private void renderObjMapper( Map<String,Object> map) {
            try {
                String json = mapper.writeValueAsString(map);
                sb.append(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        public int getMissingRefCount() {
            return missingRefCount;
        }
    }

    @Test
    public void test() {
        chkR("let x int = 15", "{'_':15}");
        chkR("let x long = 15", "{'_':15}");
        chkR("let x boolean = true", "{'_':true}");
        chkR("let x string = 'abc'", "{'_':'abc'}");
        chkR("let x date = 1481482266089", "{'_':1481482266089}");
        chkR("let x date = '2001-07-04T12:08:56.235-0700'", "{'_':994273736235}");
    }
    @Test
    public void testEnum() {
        chkR("type X enum { RED GREEN BLUE} end let x X = BLUE", "{'_':'BLUE'}");
    }
    @Test
    public void testList() {
        chkR("let x list<int> = [ 21, 24]", "{'_':[{'a':'I1:5','L':[21,24]}]}");
    }
    @Test
    public void testStruct() {
        chkR("type X struct { x int y int } end let x X = {21,24}", "{'x':21,'y':24}");
    }
    @Test
    public void testStruct2() {
        chkR("type X struct { x int y int } end type XX struct { z int xx X} end let x XX = { 20, {21,24} }", 
                "{'xx':{'x':21,'y':24},'z':20}");
    }
    @Test
    public void testStruct2UseRef() {
        DValue dval = makeDVal("type X struct { x int y int } end type XX struct { z int xx X} end let x XX = { 20, {21,24} }");
        DValue inner = dval.asStruct().getField("xx");
        DValueImpl innerImpl= (DValueImpl) inner;
        innerImpl.setPersistenceId(Long.valueOf(18));
        chkR(dval, "{'_id:xx':18,'z':20}", 0);
    }
    @Test
    public void testStruct3() {
        chkR("type X struct { x int y int } end let x list<X> = [{21,24},{33,34}]", "{'_':[{'a':'I1:5','L':[0,0]}]}", 2);
    }

    //-----
    
    private void chkR(String src, String expected) {
        chkR(src, expected, 0);
    }
    private void chkR(String src, String expected, int missingCount) {
        DValue dval = makeDVal(src);
        chkR(dval, expected, missingCount);
    }
    private void chkR(DValue dval, String expected, int missingCount) {
        DBRendr rendr = new DBRendr();
        String s = rendr.render(dval);
        s = unfix(s);
        assertEquals(expected, s);
        assertEquals(missingCount, rendr.getMissingRefCount());
    }
    private String unfix(String s) {
        return s.replace('"', '\'');
    }

    private DValue makeDVal(String src) {
        DataSet ds = load(src, true);
        return ds.getValue("x");
    }

}
