package org.dnal.compiler.parser;

import static org.junit.Assert.assertEquals;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.ViewExp;
import org.dnal.compiler.parser.ast.ViewMemberExp;
import org.junit.Test;

public class ViewParserTests {
    

    //view
	@Test
	public void test01() {
        Exp exp = ViewParser.view03().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("view X -> Y { a -> b } end");
		ViewExp ax = (ViewExp) exp;
		assertEquals("X", ax.viewName.val);
		assertEquals("Y", ax.typeName.val);
		assertEquals(ViewExp.Direction.OUTBOUND, ax.direction);
		assertEquals(1, ax.memberL.size());
		ViewMemberExp memb = ax.memberL.get(0);
		assertEquals("a", memb.left.val);
		assertEquals("b", memb.right.val);
	}
    
    
}
