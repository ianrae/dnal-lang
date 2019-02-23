package org.dnal.compiler.generate.old;

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
import org.dnal.core.nrule.NRule;
import org.dnal.core.repository.World;

public class OldDNALGeneratePhase extends ErrorTrackingBase {
    private DTypeRegistry registry;
    private World world;

    public OldDNALGeneratePhase(XErrorTracker et, DTypeRegistry registry, World world, LineLocator lineLocator) {
        super(et, lineLocator);
        this.registry = registry;
        this.world = world;
    }
    
    public boolean generate(OldOutputGenerator visitor) {
        boolean b = false;
        try {
            b = doGenerate(visitor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public boolean doGenerate(OldOutputGenerator visitor) throws Exception {
        List<DType> orderedTypeList = registry.getOrderedList();

        for(DType dtype: orderedTypeList) {
            if (TypeInfo.isBuiltIntype(dtype.getName())) {
                continue;
            }
            
            if (dtype.isStructShape()) {
                DStructType fste = (DStructType) dtype;
                visitor.startStructType(dtype.getName(), fste);
                //!!fix to be ordered
                for(String fieldName: fste.orderedList()) {
                    DType field = fste.getFields().get(fieldName);
                    visitor.structMember(fieldName, field);
                }
            } else if (dtype.isShape(Shape.ENUM)) {  
                DStructType structType = (DStructType) dtype;
                visitor.startEnumType(dtype.getName(), structType);
                for(String key: structType.orderedList()) {
                    DType elType = structType.getFields().get(key);
                    visitor.enumMember(key, elType);
                }
            } else if (dtype instanceof DListType) {
                DListType listType = (DListType) dtype;
                doList(visitor, listType);
            } else if (dtype instanceof DMapType) {
            	DMapType mapType = (DMapType) dtype;
                doMap(visitor, mapType);
            } else {
                visitor.startType(dtype.getName(), dtype);
            }

            int index = 0;
            for(NRule rule: dtype.getRawRules()) {
                String ruleText = rule.getRuleText();
                visitor.rule(index++, ruleText, rule); //fix later!! need ruleText
            }

            visitor.endType(dtype.getName(), dtype);
        }

        List<String> orderedValueList = world.getOrderedList();
        for(String valueName: orderedValueList) {
            DValue dval = world.findTopLevelValue(valueName);
            doval(visitor, 0, valueName, dval, null);
        }


        return areNoErrors();
    }
	private void doList(OldOutputGenerator visitor, DListType listType) throws Exception {
        visitor.startListType(listType.getName(), listType);
    }
    private void doMap(OldOutputGenerator visitor, DMapType mapType) throws Exception {
        visitor.startMapType(mapType.getName(), mapType);
	}

    private void doval(OldOutputGenerator visitor, int indent, String valueName, DValue dval, DValue parentVal) throws Exception {

        if (dval == null) {
            //optional field
            visitor.value(valueName, null, parentVal);
        } else if (dval.getType().isStructShape()) {
            visitor.startStruct(valueName, dval);
            
            DStructHelper helper = dval.asStruct();

            int index = 0;
            DStructType structType = (DStructType) dval.getType();
            for(String fieldName : structType.orderedList()) {
                DValue inner = helper.getField(fieldName);
                doval(visitor, indent+1, fieldName, inner, dval); //!recursion!
                index++;
            }
            visitor.endStruct(valueName, dval);
        } else if (dval.getType().isListShape()) {
            visitor.startList(valueName, dval);
            List<DValue> elementL = dval.asList();

            int index = 0;
            for(DValue el: elementL) {
                doval(visitor, indent+1, "", el, dval); //!recursion!
                index++;
            }
            visitor.endList(valueName, dval);
        } else if (dval.getType().isMapShape()) {
            visitor.startMap(valueName, dval);
            Map<String,DValue> map = dval.asMap();

            int index = 0;
            for(String key: map.keySet()) {
            	DValue el = map.get(key);            	
                doval(visitor, indent+1, key, el, dval); //!recursion!
                index++;
            }
            visitor.endMap(valueName, dval);
        } else {
//          String shape = this.doc.getShape(valueExp.type);
//          boolean isScalar = TypeInfo.isScalarType(new IdentExp(shape));
            visitor.value(valueName, dval, parentVal);
        }
    }
    
    /**
     * Output a single dval
     * @param visitor
     * @param dval
     * @return
     */
    public boolean generate(OldOutputGenerator visitor, String varName, DValue dval) {
        boolean b = false;
        try {
            b = doGenerateSingle(visitor, varName, dval);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public boolean doGenerateSingle(OldOutputGenerator visitor, String varName, DValue dval) throws Exception {
    	doval(visitor, 0, varName, dval, null);
        return areNoErrors();
    }
    
}