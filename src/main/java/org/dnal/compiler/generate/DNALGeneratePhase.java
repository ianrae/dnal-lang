package org.dnal.compiler.generate;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DStructHelper;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRule;
import org.dnal.core.repository.World;

public class DNALGeneratePhase extends ErrorTrackingBase {
    private DTypeRegistry registry;
    private World world;

    public DNALGeneratePhase(XErrorTracker et, DTypeRegistry registry, World world) {
        super(et);
        this.registry = registry;
        this.world = world;
    }
    
    public boolean generate(GenerateVisitor visitor) {
        boolean b = false;
        try {
            b = doGenerate(visitor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public boolean doGenerate(GenerateVisitor visitor) throws Exception {
        List<DType> orderedTypeList = registry.getOrderedList();

        for(DType dtype: orderedTypeList) {
            if (TypeInfo.isBuiltIntype(dtype.getName())) {
                continue;
            }
            
            if (dtype.isStructShape()) {
                visitor.startType(dtype.getName(), dtype);
                DStructType fste = (DStructType) dtype;
                //!!fix to be ordered
                for(String fieldName: fste.orderedList()) {
                    DType field = fste.getFields().get(fieldName);
                    visitor.startMember(fieldName, field);
                    visitor.endMember(fieldName, field);
                }
            } else if (dtype.isShape(Shape.ENUM)) {  
                DStructType structType = (DStructType) dtype;
                visitor.startType(dtype.getName(), dtype);
                for(String key: structType.orderedList()) {
                    DType elType = structType.getFields().get(key);
                    visitor.enumMember(key, elType);
                }
            } else if (dtype instanceof DListType) {
                DListType listType = (DListType) dtype;
                doList(visitor, listType);
            } else {
                visitor.startType(dtype.getName(), dtype);
            }

            for(NRule rule: dtype.getRawRules()) {
                String ruleText = rule.getName();
                visitor.rule(ruleText, rule); //fix later!! need ruleText
            }

            visitor.endType(dtype.getName(), dtype);
        }

        List<String> orderedValueList = world.getOrderedList();
        for(String valueName: orderedValueList) {
            DValue dval = world.findTopLevelValue(valueName);
            doval(visitor, 0, valueName, dval);
        }


        return areNoErrors();
    }
    private void doList(GenerateVisitor visitor, DListType listType)  throws Exception {
        visitor.startListType(listType.getName(), listType);
    }

    private void doval(GenerateVisitor visitor, int indent, String valueName, DValue dval) throws Exception {

        if (dval == null) {
            //optional field
            visitor.value(valueName, null);
        } else if (dval.getType().isStructShape()) {
            visitor.startStruct(valueName, dval);
            
            DValue parentDVal = dval; //dnalGenerator.findDValue(valueExp.var.name());
            DStructHelper helper = new DStructHelper(parentDVal);

            int index = 0;
            for(String fieldName : helper.getFieldNames()) {
                dval = helper.getField(fieldName);
                doval(visitor, indent+1, fieldName, dval); //!recursion!
                index++;
            }
            visitor.endStruct(valueName, dval);
        } else if (dval.getType().isShape(Shape.LIST)) {
            visitor.startList(valueName, dval);
            List<DValue> elementL = dval.asList();

            int index = 0;
            for(DValue el: elementL) {
                doval(visitor, indent+1, "", el); //!recursion!
                index++;
            }
            visitor.endList(valueName, dval);
        } else {
//          String shape = this.doc.getShape(valueExp.type);
//          boolean isScalar = TypeInfo.isScalarType(new IdentExp(shape));
            visitor.value(valueName, dval);
        }

    }
}