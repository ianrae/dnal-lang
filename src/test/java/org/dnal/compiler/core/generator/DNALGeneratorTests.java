package org.dnal.compiler.core.generator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.core.BaseTest;
import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.generate.DNALGeneratePhase;
import org.dnal.compiler.generate.OutputGenerator;
import org.dnal.compiler.generate.json.JSONGenerator;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DListType;
import org.dnal.core.DStructType;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.nrule.NRule;
import org.dnal.core.repository.World;
import org.dnal.dnalc.ConfigFileOptions;
import org.junit.Test;

public class DNALGeneratorTests extends BaseTest {
	
	public static abstract class ValueGeneratorAdaptor implements OutputGenerator {

		@Override
		public void setOptions(ConfigFileOptions configFileOptions) {
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
		public void structMember(String name, DType type) throws Exception {
		}

		@Override
		public void rule(int index, String ruleText, NRule rule) throws Exception {
		}

		@Override
		public void enumMember(String name, DType memberType) throws Exception {
		}

		@Override
		public void endType(String name, DType type) throws Exception {
		}

		@Override
		public void finish() throws Exception {
		}
	}

	public static class DNALGenerator extends ValueGeneratorAdaptor {
	    public List<String> outputL = new ArrayList<>();

		
		@Override
		public void value(String name, DValue dval, DValue parentVal) throws Exception {
			if (dval == null) {
				return;
			}
			String s = null;
			switch(dval.getType().getShape()) {
			case BOOLEAN:
				s = Boolean.valueOf(dval.asBoolean()).toString();
				break;
			case DATE:
				s = dval.asDate().toString();
				break;
			case ENUM:
				s = dval.toString();
				break;
			case INTEGER:
				s = Integer.valueOf(dval.asInt()).toString();
				break;
			case LONG:
				s = Long.valueOf(dval.asLong()).toString();
				break;
			case NUMBER:
				s = Double.valueOf(dval.asNumber()).toString();
				break;
			case STRING:
				s = dval.asString();
				break;
			default:
				break;
			}
			
			if (s != null) {
				String str = String.format("let %s %s = %s", name, dval.getType().getName(), s);
				outputL.add(str);
			}
			
		}

		@Override
		public void startStruct(String name, DValue dval) throws Exception {
		}

		@Override
		public void startList(String name, DValue value) throws Exception {
		}

		@Override
		public void endStruct(String name, DValue value) throws Exception {
		}

		@Override
		public void endList(String name, DValue value) throws Exception {
		}

	}
	
	
    @Test
	public void test() {
	    chkGen("type Foo boolean end let x Foo = false",  "let x Foo = false|", 2);
	}
	
    
//    @Test
//	public void test1() {
//	    chkGen("type Foo boolean end let x Foo = false",  "{'x':false}|", 2);
//	    chkGen("let x int = 44",  "{'x':44}|");
//	    chkGen("let x long = 555666",  "{'x':555666}|");
//	    chkGen("let x number = 3.14",  "{'x':3.14}|");
//	    chkGen("let x string = 'abc def'",  "{'x':'abc def'}|");
//		chkGen("let x date = '2017'",  "{'x':1483246800000}|");
//		chkGen("let x list<int> = [44, 45]",  "{'x':[44,45]}|");
//	}
//    @Test
//    public void test1a() {
//        chkGen("type Foo enum { RED, BLUE } end let x Foo = RED",  "{'x':'RED'}|", 2);
//    }
//
//    @Test
//    public void test2() {
//        chkGen("let x list<int> = [44, 45]", "{'x':[44,45]}|");
//        chkGen("type Z list<int> end let x list<Z> = [[44, 45],[50, 51]]",  "{'x':[[44,45],[50,51]]}|", 2);
//    }
//    @Test
//    public void test3() {
//        chkGen("type Z struct { x int, y int } end let x Z = { 15, 20 }", "{'x':15,'y':20}|", 2);
//        String s = "{'x':{'a':100,'b':101},'y':20}|";
//        chkGen("type Inner struct { a int, b int } end type Z struct { x Inner, y int } end let x Z = { { 100, 101 }, 20 }", s, 3);
//    }
//    @Test
//    public void test4() {
//        String s = "{'x':[15,16],'y':20}|";
//        chkGen("type L list<int> end type Z struct { x L, y int } end let x Z = { [15,16], 20 }", s, 3);
////        String s = "{'x':{'a':100,'b':101},'y':20}|";
////        chkGen("type Inner struct { a int b int } end type Z struct { x Inner y int } end let x Z = { { 100, 101 }, 20 }", s, 3);
//    }

    //------------------
	private void chkGen(String input, String expectedOutput) {
		chkGen(input, expectedOutput, 1);
	}
	
	private void chkGen(String input, String expectedOutput, int expectedSize) {
		ASTToDNALGenerator dnalGenerator = parseAndGenDVals(input, expectedSize);

		World world = getContext().world;
        DTypeRegistry registry = getContext().registry;
		DNALGeneratePhase phase = new DNALGeneratePhase(getContext().et, registry, world);
		DNALGenerator visitor = new DNALGenerator();
		boolean b = phase.generate(visitor);
		assertEquals(true, b);
		String output = flatten(visitor.outputL);
		log("output: " + output);
		
		String expectedJSON = fix(expectedOutput);
		assertEquals(expectedJSON, output);
	}
    private String fix(String jsonstr) {
        return jsonstr.replace("'", "\"");
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
