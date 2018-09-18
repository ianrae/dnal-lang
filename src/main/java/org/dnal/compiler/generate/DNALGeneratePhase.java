package org.dnal.compiler.generate;

import java.util.List;
import java.util.Map;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.ast.StringExp;
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

public class DNALGeneratePhase extends ErrorTrackingBase {
	    private DTypeRegistry registry;
	    private World world;
	    private LineLocator lineLocator;

	    public DNALGeneratePhase(XErrorTracker et, DTypeRegistry registry, World world, LineLocator lineLocator) {
	        super(et, null);
	        this.registry = registry;
	        this.world = world;
	        this.lineLocator = lineLocator;
	    }
	    
	    public boolean generateTypes(TypeGenerator visitor) {
	        boolean b = false;
	        try {
	            b = doGenerateTypes(visitor);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return b;
	    }
	    public boolean generateValues(ValueGenerator visitor) {
	        boolean b = false;
	        try {
	            b = doGenerateValues(visitor);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return b;
	    }
	    public boolean generateValue(ValueGenerator visitor, DValue dval, String varName) {
	        boolean b = false;
	        try {
	            b = doGenerateValue(visitor, dval, varName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return b;
	    }

	    private boolean doGenerateTypes(TypeGenerator visitor) throws Exception {
	        List<DType> orderedTypeList = registry.getOrderedList();

	        for(DType dtype: orderedTypeList) {
	        	if (TypeInfo.isBuiltIntype(dtype.getName())) {
	        		continue;
	        	}

	        	if (dtype.isStructShape()) {
	        		DStructType fste = (DStructType) dtype;
	        		String typeName = TypeInfo.parserTypeOf(fste.getCompleteName());
	        		String parentName = (fste.getBaseType() == null) ? "struct" : fste.getBaseType().getCompleteName();
	        		visitor.structType(fste, typeName, parentName);
	        	} else if (dtype.isShape(Shape.ENUM)) {  
	        		DStructType structType = (DStructType) dtype;
	        		String typeName = TypeInfo.parserTypeOf(structType.getCompleteName());
	        		visitor.enumType(structType, typeName);
	        	} else if (dtype instanceof DListType) {
	        		DListType listType = (DListType) dtype;
	        		String typeName = TypeInfo.parserTypeOf(listType.getCompleteName());
	        		String elementName = TypeInfo.parserTypeOf(listType.getElementType().getCompleteName());
	        		visitor.listType(listType, typeName, elementName);
	        	} else if (dtype instanceof DMapType) {
	        		DMapType mapType = (DMapType) dtype;
	        		String typeName = TypeInfo.parserTypeOf(mapType.getCompleteName());
	        		String elementName = TypeInfo.parserTypeOf(mapType.getElementType().getCompleteName());
	        		visitor.mapType(mapType, typeName, elementName);
	        	} else {
	        		String typeName = TypeInfo.parserTypeOf(dtype.getCompleteName());
	        		String parentName = TypeInfo.parserTypeOf(dtype.getBaseType().getCompleteName());
	        		visitor.scalarType(dtype, typeName, parentName);
	        	}
	        }
	        
	        if (! visitor.finish()) {
	        	this.addError("type-visitor finish() failed", new StringExp(""));
	        }

	        return areNoErrors();
	    }
	    
	    private boolean doGenerateValues(ValueGenerator visitor) throws Exception {
	        List<String> orderedValueList = world.getOrderedList();
	        for(String valueName: orderedValueList) {
	        	DValue dval = world.findTopLevelValue(valueName);
	        	doval(visitor, valueName, dval, null, new GeneratorContext(), 0);
	        }

	        if (! visitor.finish()) {
	        	this.addError("value-visitor finish() failed", new StringExp(""));
	        }

	        return areNoErrors();
	    }
	    
	    private boolean doGenerateValue(ValueGenerator visitor, DValue dval, String varName) throws Exception {
        	doval(visitor, varName, dval, null, new GeneratorContext(), 0);

	        if (! visitor.finish()) {
	        	this.addError("value-visitor finish() failed", new StringExp(""));
	        }

	        return areNoErrors();
	    }

	    private void doval(ValueGenerator visitor, String varName, DValue dval, String name, GeneratorContext genctx, int indexParam) throws Exception {

	        if (dval == null) {
	            //optional field
	        	if (genctx.isEquals(GeneratorContext.STRUCT)) {
	        		visitor.structMemberValue(name, dval, genctx, indexParam);
	        	} else {
	        		visitor.scalarValue(varName, dval, genctx);
	        	}
	        } else if (dval.getType().isStructShape()) {
	        	DStructType structType = (DStructType) dval.getType();
	        	ValuePlacement placement = new ValuePlacement(varName, name);
	        	visitor.startStruct(placement, dval, structType, genctx, indexParam);
	        	
	        	genctx.pushShapeCode(GeneratorContext.STRUCT);
	            DStructHelper helper = new DStructHelper(dval);

	            int index = 0;
	            for(String fieldName : structType.orderedList()) {
	                DValue inner = helper.getField(fieldName);
	                doval(visitor, null, inner, fieldName, genctx, index); //!recursion!
	                index++;
	            }
	            genctx.popShapeCode();
	            visitor.endStruct(placement, dval, structType, genctx);
	        } else if (dval.getType().isListShape()) {
	        	DListType listType = (DListType) dval.getType();
	        	ValuePlacement placement = new ValuePlacement(varName, name);
	            visitor.startList(placement, dval, listType, genctx, indexParam);
	        	genctx.pushShapeCode(GeneratorContext.LIST);
	            List<DValue> elementL = dval.asList();

	            int index = 0;
	            for(DValue el: elementL) {
	                doval(visitor, null, el, null, genctx, index); //!recursion!
	                index++;
	            }
	            genctx.popShapeCode();
	            visitor.endList(placement, dval, listType, genctx);
	        } else if (dval.getType().isMapShape()) {
	        	DMapType mapType = (DMapType) dval.getType();
	        	ValuePlacement placement = new ValuePlacement(varName, name);
	            visitor.startMap(placement, dval, mapType, genctx, indexParam);
	            genctx.pushShapeCode(GeneratorContext.MAP);
	            Map<String,DValue> map = dval.asMap();

	            int index = 0;
	            for(String key: map.keySet()) {
	            	DValue el = map.get(key);      
	            	doval(visitor, null, el, key, genctx, index); //!recursion!
	                index++;
	            }
	            genctx.popShapeCode();
	            visitor.endMap(placement, dval, mapType, genctx);
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