package org.dnal.compiler.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.dnal.api.CompilerOptions;
import org.dnal.api.impl.CompilerContext;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullAssignmentExp;
import org.dnal.compiler.parser.ast.FullStructTypeExp;
import org.dnal.compiler.parser.error.ParseErrorChecker;
import org.junit.Test;

public class CompilerTests {
	
	public static class MyDNALCompiler {
		private List<Exp> nodeL;
		private ParseErrorChecker errorChecker;
		boolean b = errorChecker.checkForErrors();

		public boolean pass1(String input) {
			nodeL = FullParser.fullParse(input);
			return true;
		}
		public boolean pass2() {
	        CompilerContext context = new CompilerContext("", new Integer(0), null, "", new CompilerOptions());
			ParseErrorChecker errorChecker = new ParseErrorChecker(nodeL, context.et, null);
			boolean b = errorChecker.checkForErrors();
			return b;
		}
	}

	@Test
	public void test1() {
		List<Exp> list = FullParser.fullParse("type X struct { } end let x int = 5");
		assertEquals(2, list.size());
		FullStructTypeExp ax = (FullStructTypeExp) list.get(0);
		assertEquals("X", ax.var.val);
		assertEquals("struct", ax.type.val);
		assertEquals(0, ax.ruleList.size());
		FullAssignmentExp bx = (FullAssignmentExp) list.get(1);
		assertEquals("x", bx.var.val);
		assertEquals("int", bx.type.val);
	}

	@Test
	public void testAlreadySeen() {
		chkParseErrors("type X struct { } end type X struct { } end", 1);
	}
	
    @Test
    public void testOptional() {
        List<Exp> list = FullParser.fullParse("type X struct { x int, y int } end let z X = { 10, 12 }");
        assertEquals(2, list.size());
        FullStructTypeExp ax = (FullStructTypeExp) list.get(0);
        assertEquals("X", ax.var.val);
        assertEquals("struct", ax.type.val);
        assertEquals(0, ax.ruleList.size());
        FullAssignmentExp bx = (FullAssignmentExp) list.get(1);
        assertEquals("z", bx.var.val);
        assertEquals("X", bx.type.val);
    }
	

	private void chkParseErrors(String input, int expected) {
		List<Exp> list = FullParser.fullParse(input);
        CompilerContext context = new CompilerContext("", new Integer(0), null, "", new CompilerOptions());
        context.et = new XErrorTracker();
		ParseErrorChecker errorChecker = new ParseErrorChecker(list, context.et, null);
		boolean b = errorChecker.checkForErrors();

		context.et.dumpErrors();
		assertEquals(expected == 0, b);
		assertEquals(expected, context.et.getErrorCount());
	}
}
