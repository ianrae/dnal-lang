package org.dnal.compiler.core.outputex;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.dnal.compiler.core.BaseTest;
import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.repository.World;
import org.dnal.outputex.DNALGeneratePhaseEx;
import org.dnal.outputex.DNALTypeGeneratorEx;
import org.dnal.outputex.DNALValueGeneratorEx;
import org.junit.Test;

public class NewGeneratorTests extends BaseTest {
	
	@Test
	public void test() {
	    chkGen("type Foo boolean end let x Foo = false",  "let x Foo = false|", 2);
	    chkGen("let x boolean = true",  "let x boolean = true|");
	    chkGen("let x int = 44",  "let x int = 44|");
	    chkGen("let x long = 555666",  "let x long = 555666|");
	    chkGen("let x number = 3.14",  "let x number = 3.14|");
	    chkGen("let x string = 'abc def'",  "let x string = 'abc def'|");
		chkGen("let x date = '2017'",  "let x date = 1483246800000|");
	}
	@Test
	public void testList() {
		chkGen("let x list<int> = [44, 45]",  "let x list<int> = [44, 45]|");
		chkGen("let x list<int> = []",  "let x list<int> = []|");
	}
	
    @Test
    public void test1a() {
        chkGen("type Foo enum { RED, BLUE } end let x Foo = RED",  "let x Foo = RED|", 2);
    }
    
    @Test
    public void test1b() {
//        chkGen("type Foo struct { name string, age int } end let x Foo = { 'amy', 33 }",  "let x Foo = {age:33, name:'amy'}|", 2);
        chkGen("type Foo struct { name string, age int } end let x Foo = { 'amy', 33 }",  "let x Foo = {name:'amy', age:33}|", 2);
    }
    @Test
    public void test2() {
        chkGen("let x list<int> = [44, 45]", "let x list<int> = [44, 45]|");
        chkGen("type Z list<int> end let x list<Z> = [[44, 45],[50, 51]]",  "let x list<Z> = [[44, 45], [50, 51]]|", 2);
    }
    @Test
    public void test3() {
        chkGen("type Z struct { x int, y int } end let x Z = { 15, 20 }", "let x Z = {x:15, y:20}|", 2);
        String s = "let x Z = {x:{a:100, b:101}, y:20}|";
        chkGen("type Inner struct { a int, b int } end type Z struct { x Inner, y int } end let x Z = { { 100, 101 }, 20 }", s, 3);
    }
    @Test
    public void test4() {
        String s = "let x Z = {x:[15, 16], y:20}|";
        chkGen("type L list<int> end type Z struct { x L, y int } end let x Z = { [15,16], 20 }", s, 3);
//        String s = "{'x':{'a':100,'b':101},'y':20}|";
//        chkGen("type Inner struct { a int b int } end type Z struct { x Inner y int } end let x Z = { { 100, 101 }, 20 }", s, 3);
    }
    
	@Test
	public void testListAny() {
		String src1 = "type Person struct { x string, y string } end ";
		src1 += "let joe Person = { 'aa', 'bb' } ";
		src1 += "type AnyList list<any> end ";
		src1 += "let people AnyList = [ joe, joe ] ";
        String s = "let joe Person = {x:'aa', y:'bb'}|let people AnyList = [{x:'aa', y:'bb'}, {x:'aa', y:'bb'}]|";
        chkGen(src1, s, 4);
	}    

    @Test
    public void test5() {
        String s = "type SizeMap map<int> end let z SizeMap = { x:33, y:34 }";
        chkGen(s, "let z SizeMap = {x:33, y:34}|", 2);
    }
    
	@Test
	public void testValueParseStructAny() {
		String src1 = "type Person struct { x string, y string } end ";
		src1 += "let joe Person = { x:'aa', y:'bb' } ";
		src1 += "type SizeMap map<any> end let z SizeMap = { 'com.x':joe }";
		
        String s = "let joe Person = {x:'aa', y:'bb'}|let z SizeMap = {com.x:{x:'aa', y:'bb'}}|";
        chkGen(src1, s, 4);
	}
    
	//---- types
	@Test
	public void testTypes() {
	    chkTypeGen("type Foo boolean end let x Foo = false",  "type Foo boolean end|", 2);
	    //type Foo int >= 5 end let x Foo = 14
	    chkTypeGen("type Foo int >= 100 end",  "type Foo int >= 100 end|", 1);
	}
	@Test
	public void testTypeList() {
		chkTypeGen("type X list<int> end",  "type X list<int> end|", 1);
	}
    @Test
    public void testTypeEnum() {
    	chkTypeGen("type Foo enum { RED, BLUE } end let x Foo = RED",  "type Foo enum {RED, BLUE} end|", 2);
    }
    @Test
    public void testTypeStruct() {
//    	chkTypeGen("type Foo struct { name string optional, age int} end",  "type Foo struct {name string optional, age int} end|", 1);
    	chkTypeGen("type Foo struct { name string optional, age int unique } end",  "type Foo struct {name string optional, age int unique} unique age end|", 1);
    }
    @Test
    public void testTypeMap() {
    	chkTypeGen("type SizeMap map<int> end",  "type SizeMap map<int> end|", 1);
    }
    
    //------------------
	private void chkGen(String input, String expectedOutput) {
		chkGen(input, expectedOutput, 1);
	}
	private void chkGen(String input, String expectedOutput, int expectedSize) {
		doChkGen(input, expectedOutput, expectedSize, false, true);
	}
	private void chkTypeGen(String input, String expectedOutput, int expectedSize) {
		doChkGen(input, expectedOutput, expectedSize, true, false);
	}	
	
	private void doChkGen(String input, String expectedOutput, int expectedSize, boolean genTypes, boolean genValues) {
		parseAndGenDVals(input, expectedSize);

		World world = getContext().world;
        DTypeRegistry registry = getContext().registry;
		DNALGeneratePhaseEx phase = new DNALGeneratePhaseEx(getContext().et, registry, world, null);
		
		if (genTypes) {
			DNALTypeGeneratorEx visitor = new DNALTypeGeneratorEx();
			boolean b = phase.generateTypes(visitor);
			assertEquals(true, b);
			String output = flatten(visitor.outputL);
			log("output: " + output);
			assertEquals(expectedOutput, output);
		} else if (genValues) {
			DNALValueGeneratorEx visitor = new DNALValueGeneratorEx();
			boolean b = phase.generateValues(visitor);
			assertEquals(true, b);
			String output = flatten(visitor.outputL);
			log("output: " + output);
			assertEquals(expectedOutput, output);
		} else {
			assertEquals(1,2); //fail
		}
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
