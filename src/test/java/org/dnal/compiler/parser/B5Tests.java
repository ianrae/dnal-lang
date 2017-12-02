package org.dnal.compiler.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullAssignmentExp;
import org.dnal.compiler.parser.ast.FullMapTypeExp;
import org.dnal.compiler.parser.ast.StructAssignExp;
import org.junit.Test;

public class B5Tests {

	@Test
	public void test50() {
		FullMapTypeExp ax = (FullMapTypeExp) FullParser.parse02("type X map<int> end");
		assertEquals("X", ax.var.val);
		assertEquals("map", ax.type.val);
		assertEquals("map<int>", ax.elementType.name());
		assertEquals(0, ax.ruleList.size());
	}
	
	@Test
	public void test50a() {
		FullAssignmentExp ax = FullParser.parse01("let x map<int> = { x:34 }");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof StructAssignExp);
		StructAssignExp mapexp = (StructAssignExp) ax.value;
		assertEquals(1, mapexp.list.size());
	}

	@Test
	public void test100() {
		FullMapTypeExp ax = (FullMapTypeExp) FullParser.parse02("type X map<map<int>> end");
		assertEquals("X", ax.var.val);
		assertEquals("map", ax.type.val);
		assertEquals("map<map<int>>", ax.elementType.name());
		assertEquals(0, ax.ruleList.size());
	}
	
	@Test
	public void test100a() {
		List<Exp> axlist = FullParser.fullParse("type X map<map<int>> end");
		assertEquals(1, axlist.size());
		FullMapTypeExp ax = (FullMapTypeExp) axlist.get(0);
		
		assertEquals("X", ax.var.val);
		assertEquals("map", ax.type.val);
		assertEquals("map<map<int>>", ax.elementType.name());
		assertEquals(0, ax.ruleList.size());
	}

}
