package org.dnal.outputex;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.compiler.parser.error.LineLocator;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.repository.World;

public class NewDNALGeneratePhase extends ErrorTrackingBase {
	    private DTypeRegistry registry;
	    private World world;
	    private LineLocator lineLocator;

	    public NewDNALGeneratePhase(XErrorTracker et, DTypeRegistry registry, World world, LineLocator lineLocator) {
	        super(et, null);
	        this.registry = registry;
	        this.world = world;
	        this.lineLocator = lineLocator;
	    }
	    
	    public boolean generate(OutputGeneratorEx visitor, OutputOptions outputOptions) {
	        boolean b = false;
	        try {
	            b = doGenerate(visitor, outputOptions);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return b;
	    }

	    public boolean doGenerate(OutputGeneratorEx visitor, OutputOptions outputOptions) throws Exception {
	        List<DType> orderedTypeList = registry.getOrderedList();

	        boolean doTypes = outputOptions.equals(OutputOptions.ALL) || outputOptions.equals(outputOptions.TYPES_ONLY);
	        if (doTypes) {
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
	        }

	        boolean doValues = outputOptions.equals(OutputOptions.ALL) || outputOptions.equals(outputOptions.VALUES_ONLY);
	        if (doValues) {
	        	List<String> orderedValueList = world.getOrderedList();
	        	for(String valueName: orderedValueList) {
	        		DValue dval = world.findTopLevelValue(valueName);
//	            doval(visitor, 0, valueName, dval, null);
	        		String typeName = TypeInfo.parserTypeOf(dval.getType().getName());
	        		visitor.topLevelValue(valueName, dval, typeName);
	        	}
	        }

	        return areNoErrors();
	    }

//	    private void doval(OutputGenerator visitor, int indent, String valueName, DValue dval, DValue parentVal) throws Exception {
//
//	        if (dval == null) {
//	            //optional field
//	            visitor.value(valueName, null, parentVal);
//	        } else if (dval.getType().isStructShape()) {
//	            visitor.startStruct(valueName, dval);
//	            
//	            DStructHelper helper = new DStructHelper(dval);
//
//	            int index = 0;
//	            DStructType structType = (DStructType) dval.getType();
//	            for(String fieldName : structType.orderedList()) {
//	                DValue inner = helper.getField(fieldName);
//	                doval(visitor, indent+1, fieldName, inner, dval); //!recursion!
//	                index++;
//	            }
//	            visitor.endStruct(valueName, dval);
//	        } else if (dval.getType().isListShape()) {
//	            visitor.startList(valueName, dval);
//	            List<DValue> elementL = dval.asList();
//
//	            int index = 0;
//	            for(DValue el: elementL) {
//	                doval(visitor, indent+1, "", el, dval); //!recursion!
//	                index++;
//	            }
//	            visitor.endList(valueName, dval);
//	        } else if (dval.getType().isMapShape()) {
//	            visitor.startMap(valueName, dval);
//	            Map<String,DValue> map = dval.asMap();
//
//	            int index = 0;
//	            for(String key: map.keySet()) {
//	            	DValue el = map.get(key);            	
//	                doval(visitor, indent+1, key, el, dval); //!recursion!
//	                index++;
//	            }
//	            visitor.endMap(valueName, dval);
//	        } else {
////	          String shape = this.doc.getShape(valueExp.type);
////	          boolean isScalar = TypeInfo.isScalarType(new IdentExp(shape));
//	            visitor.value(valueName, dval, parentVal);
//	        }
//	    }
	}