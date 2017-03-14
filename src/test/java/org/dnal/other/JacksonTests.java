package org.dnal.other;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTests {


    public static class MyValue {
        private String name;
        private  int age;

        public MyValue() {
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
    }
    @Test
    public void test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
//        MyValue value = mapper.readValue("{\"name\":\"Bob\", \"age\":13}", MyValue.class);  
        
        String json = fix("{\'name\':\'Bob\', \'age\':13}");
        MyValue value = mapper.readValue(json, MyValue.class);  
        assertEquals("Bob", value.getName());
    }
    
    
    @Test
    public void testMap() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        Map<String,Object> map = new HashMap<>();
        map.put("name", "Bob");
        map.put("age", 13);

        String json = mapper.writeValueAsString(map);
        log(json);
        
        @SuppressWarnings("unchecked")
        Map<String,Object> map2 = mapper.readValue(json, Map.class);
        assertEquals("Bob", map2.get("name"));
        assertEquals(13, map2.get("age"));
    }
    
    private void log(String json) {
        System.out.println(json);
    }


    private String fix(String jsonstr) {
        return jsonstr.replace("'", "\"");
    }

//----------------------------------------
    /*
     * storage json. render a dnal into json string
     * let x int = 15
     * 
     * { "_": 15 }
     * { "_": [ {"a": "I1:5", "L": [ 15, 16 ]}  ] }
     * { "field1", 34, ... }
     * 
     * 
     * 
     * 
     */
    
}
