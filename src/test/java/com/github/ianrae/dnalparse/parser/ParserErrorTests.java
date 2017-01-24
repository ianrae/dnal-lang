package com.github.ianrae.dnalparse.parser;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.ianrae.dnalparse.CompilerOptions;
import com.github.ianrae.dnalparse.et.XErrorTracker;
import com.github.ianrae.dnalparse.impl.CompilerContext;
import com.github.ianrae.dnalparse.parser.ast.Exp;
import com.github.ianrae.dnalparse.parser.ast.FullAssignmentExp;
import com.github.ianrae.dnalparse.parser.ast.FullStructTypeExp;
import com.github.ianrae.dnalparse.parser.ast.IdentExp;
import com.github.ianrae.dnalparse.parser.error.ParseErrorChecker;

public class ParserErrorTests {

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
	public void test2() {
		chkParseErrors("type X struct { } end", 0);
		chkParseErrors("type int struct { } end", 1);
		chkParseErrors("type X Colour end", 1);
	}

	@Test
	public void testAlreadySeen() {
		chkParseErrors("type X struct { } end type X struct { } end", 1);
	}

	@Test
	public void testVar1() {
		chkParseErrors("let int int = 5", 1);
		chkParseErrors("let x Colour = 5", 1);
	}

	@Test
	public void testVar2() {
		chkParseErrors("let y int = false", 1);
		chkParseErrors("let b1 boolean = 5", 1);
		chkParseErrors("let s1 string = 5", 1);
		chkParseErrors("let persons list<string> = [ 'abc' ]", 0);
	}
	
	@Test
	public void testVarStruct() {
//		chkParseErrors("let bob struct = 5", 1);  //is a jparsec parse error
		chkParseErrors("type Person struct { name string } end let bob Person = { 'bob' }", 0);
		chkParseErrors("type Person struct { name string } end let bob Person = { name:'bob' }", 0);
		chkParseErrors("type Person struct {  } end let bob Person = {  }", 0);
	}
	
	@Test
	public void testVarShape() {
		chkParseErrors("type Pos int end let y Pos = false", 1);
	}

	@Test
	public void testShape() {
		ParseErrorChecker errorChecker  = chkParseErrors("type Pos int end let y Pos = false", 1);
		IdentExp ident = new IdentExp("Pos");
		String shape = errorChecker.getDoc().getShape(ident);
		assertEquals("int", shape);
	}
	
	
    @Test
    public void testList() {
        chkParseErrors("type X list<int> end let y X = [ ]", 0);
        chkParseErrors("type X list<int> end let y X = 15", 1);
        chkParseErrors("type Z int end let y list<Z> = [ ]", 0);
        chkParseErrors("type Z int end let y list<QQ> = [ ]", 1);

        chkParseErrors("type Z struct { size int } end let y list<Z> = [ ]", 0);
        chkParseErrors("type Z struct { size int }  end let y list<QQ> = [ ]", 1);

    }
	

	private ParseErrorChecker chkParseErrors(String input, int expected) {
		List<Exp> list = FullParser.fullParse(input);
		CompilerContext context = new CompilerContext("", new Integer(0), null, "", new CompilerOptions());
		context.et = new XErrorTracker();
		ParseErrorChecker errorChecker = new ParseErrorChecker(list, context.et);
		boolean b = errorChecker.checkForErrors();

		context.et.dumpErrors();
		assertEquals(expected == 0, b);
		assertEquals(expected, context.et.getErrorCount());
		return errorChecker;
	}
}
