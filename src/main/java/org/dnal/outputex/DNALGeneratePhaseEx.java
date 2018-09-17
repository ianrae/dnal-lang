package org.dnal.outputex;

import java.util.List;
import java.util.Map;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.compiler.parser.error.LineLocator;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructHelper;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.repository.World;

public class DNALGeneratePhaseEx extends ErrorTrackingBase {
	    private DTypeRegistry registry;
	    private World world;
	    private LineLocator lineLocator;

	    public DNALGeneratePhaseEx(XErrorTracker et, DTypeRegistry registry, World world, LineLocator lineLocator) {
	        super(et, null);
	        this.registry = registry;
	        this.world = world;
	        this.lineLocator = lineLocator;
	    }
	    
	    public boolean generateTypes(TypeGeneratorEx visitor) {
	        boolean b = false;
	        try {
	            b = doGenerateTypes(visitor);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return b;
	    }
	    public boolean generateValues(ValueGeneratorEx visitor) {
	        boolean b = false;
	        try {
	            b = doGenerateValues(visitor);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return b;
	    }

	    private boolean doGenerateTypes(TypeGeneratorEx visitor) throws Exception {
	        List<DType> orderedTypeList = registry.getOrderedList();

	        for(DType dtype: orderedTypeList) {
	        	if (TypeInfo.isBuiltIntype(dtype.getName())) {
	        		continue;
	        	}

	        	if (dtype.isStructShape()) {
	        		DStructType fste = (DStructType) dtype;
	        		String typeName = TypeInfo.parserTypeOf(fste.getName());
	        		String parentName = (fste.getBaseType() == null) ? "struct" : fste.getBaseType().getName();
	        		visitor.structType(fste, typeName, parentName);
	        	} else if (dtype.isShape(Shape.ENUM)) {  
	        		DStructType structType = (DStructType) dtype;
	        		String typeName = TypeInfo.parserTypeOf(structType.getName());
	        		visitor.enumType(structType, typeName);
	        	} else if (dtype instanceof DListType) {
	        		DListType listType = (DListType) dtype;
	        		String typeName = TypeInfo.parserTypeOf(listType.getName());
	        		String elementName = TypeInfo.parserTypeOf(listType.getElementType().getName());
	        		visitor.listType(listType, typeName, elementName);
	        	} else if (dtype instanceof DMapType) {
	        		DMapType mapType = (DMapType) dtype;
	        		visitor.mapType(mapType);
	        	} else {
	        		String typeName = TypeInfo.parserTypeOf(dtype.getName());
	        		String parentName = TypeInfo.parserTypeOf(dtype.getBaseType().getName());
	        		visitor.scalarType(dtype, typeName, parentName);
	        	}
	        }

	        return areNoErrors();
	    }
	    
	    private boolean doGenerateValues(ValueGeneratorEx visitor) throws Exception {
	        List<DType> orderedTypeList = registry.getOrderedList();

	        List<String> orderedValueList = world.getOrderedList();
	        for(String valueName: orderedValueList) {
	        	DValue dval = world.findTopLevelValue(valueName);
	        	doval(visitor, valueName, dval, null, new GeneratorContext(), 0);
	        }

	        return areNoErrors();
	    }

	    private void doval(ValueGeneratorEx visitor, String varName, DValue dval, String name, GeneratorContext genctx, int indexParam) throws Exception {

	        if (dval == null) {
	            //optional field
	            visitor.scalarValue(varName, dval, genctx);
	        } else if (dval.getType().isStructShape()) {
	        	DStructType structType = (DStructType) dval.getType();
	        	visitor.startStruct(varName, name, dval, structType, genctx, indexParam);
	        	
	        	genctx.pushShapeCode(GeneratorContext.STRUCT);
	            DStructHelper helper = new DStructHelper(dval);

	            int index = 0;
	            for(String fieldName : structType.orderedList()) {
	                DValue inner = helper.getField(fieldName);
	                doval(visitor, null, inner, fieldName, genctx, index); //!recursion!
	                index++;
	            }
	            genctx.popShapeCode();
	            visitor.endStruct(dval, structType, genctx);
	        } else if (dval.getType().isListShape()) {
	        	DListType listType = (DListType) dval.getType();
	            visitor.startList(varName, name, dval, listType, genctx, indexParam);
	        	genctx.pushShapeCode(GeneratorContext.LIST);
	            List<DValue> elementL = dval.asList();

	            int index = 0;
	            for(DValue el: elementL) {
	                doval(visitor, null, el, null, genctx, index); //!recursion!
	                index++;
	            }
	            genctx.popShapeCode();
	            visitor.endList(dval, listType, genctx);
	        } else if (dval.getType().isMapShape()) {
	        	DMapType mapType = (DMapType) dval.getType();
	            visitor.startMap(varName, name, dval, mapType, genctx, indexParam);
	            genctx.pushShapeCode(GeneratorContext.MAP);
	            Map<String,DValue> map = dval.asMap();

	            int index = 0;
	            for(String key: map.keySet()) {
	            	DValue el = map.get(key);      
	            	doval(visitor, null, el, key, genctx, index);
	                index++;
	            }
	            genctx.popShapeCode();
	            visitor.endMap(dval, mapType, genctx);
	        } else {
	        	if (genctx.isEquals(GeneratorContext.STRUCT)) {
	        		visitor.structMemberValue(name, dval, genctx, indexParam);
	        	} else if (genctx.isEquals(GeneratorContext.LIST)) {
	        		visitor.listElementValue(dval, genctx, indexParam);
	        	} else if (genctx.isEquals(GeneratorContext.MAP)) {
	        		visitor.mapMemberValue(name, dval, genctx, indexParam);
	        	} else {
	        		visitor.scalarValue(varName, dval, genctx);
	        	}
	        }
	    }
	}