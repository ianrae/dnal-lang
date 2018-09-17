package org.dnal.compiler.core.outputex;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.dnal.compiler.core.BaseTest;
import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.generate.DNALGeneratePhase;
import org.dnal.compiler.generate.SimpleFormatOutputGenerator;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DTypeRegistry;
import org.dnal.outputex.DNALGeneratePhaseEx;
import org.dnal.outputex.SimpleFormatOutputGeneratorEx;
import org.junit.Test;

public class SimpleGeneratorTests extends BaseTest {

	@Test
	public void test1() {
		chkGen("type Foo boolean end",  "type:Foo:boolean|endtype|");
		chkGen("type Foo int end", 		"type:Foo:int|endtype|");
		chkGen("type Foo long end", 		"type:Foo:long|endtype|");
		chkGen("type Foo string end", 	"type:Foo:string|endtype|");
	}

	@Test
	public void testRules() {
		chkGen("type X int > 0,  < 5 end", "type:X:int| r: > 0| r: < 5|endtype|");
	}

	@Test
	public void testStruct() {
		chkGen("type Foo struct {  } end", "type:Foo:struct|endtype|");
		chkGen("type Foo struct { x int, y string } end", "type:Foo:struct| x:int| y:string|endtype|");
	}

	@Test
	public void testEnum() {
		chkGen("type Foo enum {  } end", "type:Foo:enum|endtype|");
		chkGen("type Foo enum { RED, BLUE } end", "type:Foo:enum| RED:string| BLUE:string|endtype|");
	}

	@Test
	public void testList() {
		chkGen("type Foo list<int> end", "type:Foo:list<int>|endtype|");
//		chkGen("type Foo enum { RED BLUE } end", "type:Foo:enum| RED:string| BLUE:string|endtype|");
	}

	@Test
	public void testVal() {
		chkGen("type Foo boolean end let x Foo = true", "type:Foo:boolean|endtype|value:x:Foo:true|", 2);
		chkGen("type Foo int end let x Foo = 54", 		"type:Foo:int|endtype|value:x:Foo:54|", 2);
		chkGen("type Foo string end let x Foo = 'abc'", "type:Foo:string|endtype|value:x:Foo:abc|", 2);
	}

	@Test
	public void testStructVal() {
		String s1 = "type:Foo:struct| x:int| y:int|endtype|";
		String s2 = "value:x:Foo {| vx:10| vy:11|}|";
		chkGen("type Foo struct { x int, y int } end let x Foo = { 10, 11 }", s1 + s2, 2);
	}
	
    @Test
    public void testStructValOptional() {
        String s1 = "type:Foo:struct| x:int| y:int|endtype|";
        String s2 = "value:x:Foo {| vx:10| vy:11|}|";
        chkGen("type Foo struct { x int optional, y int } end let x Foo = { 10, 11 }", s1 + s2, 2);
    }
    @Test
    public void testStructValOptional2() {
        String s1 = "type:Foo:struct| x:int| y:int|endtype|";
        String s2 = "value:x:Foo {| vx:null| vy:11|}|";
        chkGen("type Foo struct { x int optional, y int } end let x Foo = { null, 11 }", s1 + s2, 2);
    }
    @Test
    public void testStructValOptional2a() {
        String s1 = "type:Foo:struct| x:int| y:int|endtype|";
        String s2 = "value:x:Foo {| vx:null| vy:11|}|";
        chkGen("type Foo struct { x int optional, y int } end let x Foo = { x:null, y:11 }", s1 + s2, 2);
    }
	
	@Test
	public void testListVal() {
		String s1 = "type:Foo:list<int>|endtype|";
		String s2 = "value:x:Foo [| 10| 11|]|";
		chkGen("type Foo list<int> end let x Foo = [ 10, 11]", s1 + s2, 2);
	}

	private void chkGen(String input, String expectedOutput) {
		chkGen(input, expectedOutput, 1);
	}
	
	private void chkGen(String input, String expectedOutput, int expectedSize) {
		ASTToDNALGenerator dnalGenerator = parseAndGenDVals(input, expectedSize);

        DTypeRegistry registry = getContext().registry;
//		DNALGeneratePhase phase = new DNALGeneratePhase(getContext().et, registry, getContext().world, null);
		DNALGeneratePhaseEx phase = new DNALGeneratePhaseEx(getContext().et, registry, getContext().world, null);
		SimpleFormatOutputGeneratorEx visitor = new SimpleFormatOutputGeneratorEx();
		boolean b = phase.generateTypes(visitor);
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
