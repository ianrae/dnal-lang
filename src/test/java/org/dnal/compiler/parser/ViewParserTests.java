package org.dnal.compiler.parser;

import static org.junit.Assert.assertEquals;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.ViewDirection;
import org.dnal.compiler.parser.ast.ViewExp;
import org.dnal.compiler.parser.ast.ViewMemberExp;
import org.junit.Test;

public class ViewParserTests {
    
    //output view
	@Test
	public void test01() {
        Exp exp = ViewParser.view03().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("outview X -> Y { a -> b int } end");
		ViewExp ax = (ViewExp) exp;
		assertEquals("X", ax.typeName.val);
		assertEquals("Y", ax.viewName.val);
		assertEquals(ViewDirection.OUTBOUND, ax.direction);
		assertEquals(1, ax.memberL.size());
		ViewMemberExp memb = ax.memberL.get(0);
		assertEquals("a", memb.left.val);
		assertEquals("b", memb.right.val);
		assertEquals(ViewDirection.OUTBOUND, memb.direction);
	}
    
    //input view
	@Test
	public void test10() {
        Exp exp = ViewParser.view03().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("inview X <- Y { a <- b string c <- d int} end");
		ViewExp ax = (ViewExp) exp;
		assertEquals("X", ax.typeName.val);
		assertEquals("Y", ax.viewName.val);
		assertEquals(ViewDirection.INBOUND, ax.direction);
		assertEquals(2, ax.memberL.size());
		ViewMemberExp memb = ax.memberL.get(0);
		assertEquals("a", memb.left.val);
		assertEquals("b", memb.right.val);
		assertEquals(ViewDirection.INBOUND, memb.direction);
		memb = ax.memberL.get(1);
		assertEquals("c", memb.left.val);
		assertEquals("d", memb.right.val);
		assertEquals(ViewDirection.INBOUND, memb.direction);
	}
    
    
}
