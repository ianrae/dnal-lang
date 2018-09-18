package org.dnal.compiler.core.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.dnal.compiler.core.BaseTest;
import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.generate.DNALGeneratePhaseEx;
import org.dnal.compiler.generate.JSONValueGeneratorEx;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.error.LineLocator;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.repository.World;
import org.junit.Test;

public class SingleValueJSONTests extends BaseTest {
	
	@Test
	public void test() {
	    chkGen("type Foo boolean end let x Foo = false", "x", "{'x': false}|", 2);
	    chkGen("let x int = 44",  "x", "{'x': 44}|");
	    chkGen("let x long = 555666",  "x", "{'x': 555666}|");
	    chkGen("let x number = 3.14", "x",  "{'x': 3.14}|");
	    chkGen("let x string = 'abc def'",  "x", "{'x': \"abc def\"}|");
		chkGen("let x date = '2017'",  "x", "{'x': 1483246800000}|");
		chkGen("let x list<int> = [44, 45]",  "x", "{'x': [44, 45]}|");
	}
    
    //------------------
	private void chkGen(String input, String varName, String expectedOutput) {
		chkGen(input, varName, expectedOutput, 1);
	}
	
    private String fix(String jsonstr) {
        return jsonstr.replace("'", "\"");
    }
	private void chkGen(String input, String varName, String expectedOutput, int expectedSize) {
		ASTToDNALGenerator dnalGenerator = parseAndGenDVals(input, expectedSize);

		World world = getContext().world;
        DTypeRegistry registry = getContext().registry;
        LineLocator lineLocator = new LineLocator(input);
		DNALGeneratePhaseEx phase = new DNALGeneratePhaseEx(getContext().et, registry, world, lineLocator);
		JSONValueGeneratorEx visitor = new JSONValueGeneratorEx();
		
		DValue dval = world.findTopLevelValue(varName);
		assertNotNull(dval);
		boolean b = phase.generateValue(visitor, dval, varName);
		assertEquals(true, b);
		String output = flatten(visitor.outputL);
		log("output: " + output);
		expectedOutput = fix(expectedOutput);
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
