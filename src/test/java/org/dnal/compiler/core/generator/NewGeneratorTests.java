package org.dnal.compiler.core.generator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.core.BaseTest;
import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.error.ErrorTrackingBase;
import org.dnal.compiler.parser.error.TypeInfo;
import org.dnal.core.DListType;
import org.dnal.core.DMapType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.repository.World;
import org.junit.Test;

public class NewGeneratorTests extends BaseTest {
	
	public static class NewOutputGenerator {
	    public List<String> outputL = new ArrayList<>();
		
		
		public void structType(DStructType dtype) {
		}
		public void enumType(DStructType enumType) {
			
		}
		public void listType(DListType listType) {
			// TODO Auto-generated method stub
			
		}
		public void mapType(DMapType mapType) {
			// TODO Auto-generated method stub
			
		}
		public void scalarType(DType dtype) {
			// TODO Auto-generated method stub
			
		}
		public void typeLevelValue(String varName, DValue dval) {
			
			String typeName = dval.getType().getName();
			String valueStr = getValueStr(dval);
			String s = String.format("let %s %s = %s", varName, typeName, valueStr);
			
			outputL.add(s);
		}
		private String getValueStr(DValue dval) {
			if (dval.getObject() == null) {
				return "null";
			}
			
			String s = null;
			if (dval.getType().isScalarShape()) {
				switch (dval.getType().getShape()) {
					case BOOLEAN:
						s = Boolean.valueOf(dval.asBoolean()).toString();
						break;
					default:
						break;
				}
			}
			return s;
		}
	}
	
	public static class NewDNALGeneratePhase extends ErrorTrackingBase {
	    private DTypeRegistry registry;
	    private World world;

	    public NewDNALGeneratePhase(XErrorTracker et, DTypeRegistry registry, World world) {
	        super(et);
	        this.registry = registry;
	        this.world = world;
	    }
	    
	    public boolean generate(NewOutputGenerator visitor) {
	        boolean b = false;
	        try {
	            b = doGenerate(visitor);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return b;
	    }

	    public boolean doGenerate(NewOutputGenerator visitor) throws Exception {
	        List<DType> orderedTypeList = registry.getOrderedList();

	        for(DType dtype: orderedTypeList) {
	            if (TypeInfo.isBuiltIntype(dtype.getName())) {
	                continue;
	            }
	            
	            if (dtype.isStructShape()) {
	                DStructType fste = (DStructType) dtype;
	                visitor.structType(fste);
//	                visitor.startStructType(dtype.getName(), fste);
//	                //!!fix to be ordered
//	                for(String fieldName: fste.orderedList()) {
//	                    DType field = fste.getFields().get(fieldName);
//	                    visitor.structMember(fieldName, field);
//	                }
	            } else if (dtype.isShape(Shape.ENUM)) {  
	                DStructType structType = (DStructType) dtype;
	                visitor.enumType(structType);
//	                visitor.startEnumType(dtype.getName(), structType);
//	                for(String key: structType.orderedList()) {
//	                    DType elType = structType.getFields().get(key);
//	                    visitor.enumMember(key, elType);
//	                }
	            } else if (dtype instanceof DListType) {
	                DListType listType = (DListType) dtype;
	                visitor.listType(listType);
	            } else if (dtype instanceof DMapType) {
	            	DMapType mapType = (DMapType) dtype;
	                visitor.mapType(mapType);
	            } else {
	                visitor.scalarType(dtype);
	            }

//	            int index = 0;
//	            for(NRule rule: dtype.getRawRules()) {
//	                String ruleText = rule.getRuleText();
//	                visitor.rule(index++, ruleText, rule); //fix later!! need ruleText
//	            }

//	            visitor.endType(dtype.getName(), dtype);
	        }

	        List<String> orderedValueList = world.getOrderedList();
	        for(String valueName: orderedValueList) {
	            DValue dval = world.findTopLevelValue(valueName);
//	            doval(visitor, 0, valueName, dval, null);
	            visitor.typeLevelValue(valueName, dval);
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
	
	
	
	@Test
	public void test() {
	    chkGen("type Foo boolean end let x Foo = false",  "let x Foo = false|", 2);
//	    chkGen("let x int = 44",  "let x int = 44|");
//	    chkGen("let x long = 555666",  "let x long = 555666|");
//	    chkGen("let x number = 3.14",  "let x number = 3.14|");
//	    chkGen("let x string = 'abc def'",  "let x string = 'abc def'|");
//		chkGen("let x date = '2017'",  "let x date = 1483246800000|");
//		chkGen("let x list<int> = [44, 45]",  "let x list<int> = [44, 45]|");
	}
    
    
    //------------------
	private void chkGen(String input, String expectedOutput) {
		chkGen(input, expectedOutput, 1);
	}
	
	private void chkGen(String input, String expectedOutput, int expectedSize) {
		ASTToDNALGenerator dnalGenerator = parseAndGenDVals(input, expectedSize);

		World world = getContext().world;
        DTypeRegistry registry = getContext().registry;
		NewDNALGeneratePhase phase = new NewDNALGeneratePhase(getContext().et, registry, world);
		NewOutputGenerator visitor = new NewOutputGenerator();
		boolean b = phase.generate(visitor);
		assertEquals(true, b);
		String output = flatten(visitor.outputL);
		log("output: " + output);
		
		assertEquals(expectedOutput, output);
	}

	private ASTToDNALGenerator parseAndGenDVals(String input, int expectedSize) {
		log("doing: " + input);
		List<Exp> list = FullParser.fullParse(input);
		assertEquals(expectedSize, list.size());

		ASTToDNALGenerator generator = createASTGenerator();
		boolean b = generator.generate(list);
		assertEquals(true, b);
		return generator;
	}

	private String flatten(List<String> L) {
		StringBuffer sb = new StringBuffer();
		for(String s: L) {
			sb.append(s);
			sb.append("|");
		}
		return sb.toString();
	}


	private void log(String s) {
		System.out.println(s);
	}
}
