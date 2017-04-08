package org.dnal.compiler.generate.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dnal.compiler.codegen.java.JavaOutputRenderer;
import org.dnal.compiler.generate.GenerateVisitor;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DValue;
import org.dnal.core.logger.Log;
import org.dnal.core.nrule.NRule;
import org.dnal.core.util.TextFileWriter;
import org.dnal.dnalc.ConfigFileOptions;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONGenerator implements GenerateVisitor {
    ObjectMapper mapper = new ObjectMapper();
    public List<String> outputL = new ArrayList<>();
    private Stack<String> shapeStack = new Stack<>();
    private Stack<List<Object>> listStack = new Stack<>();
//    private ListStack listStack = new ListStack();
//    private MapStack mapStack = new MapStack();
    private Stack<Map<String,Object>> mapStack = new Stack<>();
    protected ConfigFileOptions options;


	public JSONGenerator() {
		this(null);
	}
	public JSONGenerator(ConfigFileOptions configFileOptions) {
		this.options = configFileOptions;
	}
	@Override
    public void startStructType(String name, DStructType dtype) throws Exception {
    }
    @Override
    public void startEnumType(String name, DStructType dtype) throws Exception {
    }
    @Override
    public void startType(String name, DType dtype) throws Exception {
    }
    @Override
    public void startListType(String name, DListType type) throws Exception {
    }
    @Override
    public void endType(String name, DType type) throws Exception {
    }
    @Override
    public void structMember(String name, DType type) throws Exception {
    }
    @Override
    public void rule(int index, String ruleText, NRule rule) throws Exception {
    }
    @Override
    public void enumMember(String name, DType memberType) throws Exception {
    }

    private String getCurrentShape() {
        String shape = (shapeStack.isEmpty()) ? "" : shapeStack.peek();
        return shape;
    }
    @Override
    public void value(String name, DValue dval, DValue parentVal) throws Exception {
        String shape = getCurrentShape();
        Map<String,Object> mmm = new HashMap<>();

        if (shape.equals("L")) {
            listStack.peek().add(dval.getObject());
            return;
        } else if (shape.equals("S")) {
            mapStack.peek().put(name, dval.getObject());
            return;
        } else {
            mmm.put(name, dval.getObject());
        }
        String json = "";
        //can throw a JsonProcessingException 
        json = mapper.writeValueAsString(mmm);
        outputL.add(json);
    }


    @Override
    public void startList(String name, DValue value) throws Exception {
        List<Object> L = new ArrayList<>();
        listStack.push(L);
        shapeStack.push("L");
    }

    @Override
    public void endList(String name, DValue value) throws Exception {
        shapeStack.pop();
        String shape = getCurrentShape();
        
        List<Object> L = listStack.pop();
        if (shape.equals("L")) {
            if (listStack.size() >= 0) {
                List<Object> top = listStack.peek();
                top.add(L);
                return;
            }
        } else if (shape.equals("S")) {
            if (mapStack.size() > 0) {
                Map<String,Object> top = mapStack.peek();
                top.put(name, L);
                return;
            }
        } else {
            if (mapStack.size() > 0) {
                throw new RuntimeException("Error in endList!");
            }
        }        
        
        Map<String,Object> mmm = new HashMap<>();
        mmm.put(name, L);
        String json = "";
        json = mapper.writeValueAsString(mmm);
        outputL.add(json);

    }

    @Override
    public void startStruct(String name, DValue dval) throws Exception {
        Map<String,Object> mmm = new HashMap<>();
        mapStack.push(mmm);
        shapeStack.push("S");
    }

    @Override
    public void endStruct(String name, DValue value) throws Exception {
        shapeStack.pop();
        String shape = getCurrentShape();
        
        Map<String,Object> mmm = mapStack.pop();
        if (shape.equals("L")) {
            if (mapStack.size() >= 0) {
                List<Object> top = listStack.peek();
                top.add(mmm);
                return;
            }
        } else if (shape.equals("S")) {
            if (mapStack.size() > 0) {
                Map<String,Object> top = mapStack.peek();
                top.put(name, mmm);
                return;
            }
        } else {
            if (mapStack.size() > 0) {
                throw new RuntimeException("Error in endMap!");
            }
        }
        
        Map<String,Object> mmmx = new HashMap<>();
        mmmx.put(name, mmm);
        String json = "";
        json = mapper.writeValueAsString(mmm);
        outputL.add(json);
    }

    @Override
    public void finish() throws Exception {
    	if (options != null && options.outputPath != null) {
    		writeFile(options.outputPath);
    	} else {
    		Log.log(" ");
    		Log.log("output: ");
    		for(String line: this.outputL) {
    			Log.log(" " + line);
    		}
    	}
    	
    }
    
    protected boolean writeFile(String path) {
        Log.log("writing " + path);
        TextFileWriter w = new TextFileWriter();
        return w.writeFile(path, outputL);
    }
}