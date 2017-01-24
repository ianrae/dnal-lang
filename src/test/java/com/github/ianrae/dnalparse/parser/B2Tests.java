package com.github.ianrae.dnalparse.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.TerminalParser;
import org.dnal.compiler.parser.VarParser;
import org.dnal.compiler.parser.ast.BooleanExp;
import org.dnal.compiler.parser.ast.ComparisonOrRuleExp;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.FullAssignmentExp;
import org.dnal.compiler.parser.ast.FullEnumTypeExp;
import org.dnal.compiler.parser.ast.FullListTypeExp;
import org.dnal.compiler.parser.ast.FullStructTypeExp;
import org.dnal.compiler.parser.ast.FullTypeExp;
import org.dnal.compiler.parser.ast.IdentExp;
import org.dnal.compiler.parser.ast.ImportExp;
import org.dnal.compiler.parser.ast.IntegerExp;
import org.dnal.compiler.parser.ast.IsaRuleExp;
import org.dnal.compiler.parser.ast.ListAssignExp;
import org.dnal.compiler.parser.ast.LongExp;
import org.dnal.compiler.parser.ast.NumberExp;
import org.dnal.compiler.parser.ast.PackageExp;
import org.dnal.compiler.parser.ast.StringExp;
import org.dnal.compiler.parser.ast.StructAssignExp;
import org.dnal.compiler.parser.ast.StructMemberAssignExp;
import org.dnal.compiler.parser.ast.StructMemberExp;
import org.dnal.compiler.parser.ast.ViaExp;
import org.junit.Test;

import com.github.ianrae.dnalparse.ListChecker;

public class B2Tests {

	@Test
	public void test1() {
		IdentExp ax = (IdentExp) VarParser.assignmentUnused().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("let x");
		assertEquals("x", ax.val);
	}

	@Test
	public void test3() {
		FullAssignmentExp ax = (FullAssignmentExp) VarParser.assignmentUnused0().from(TerminalParser.tokenizer, TerminalParser.ignored.skipMany()).parse("let x int");
		assertEquals("x", ax.var.val);
		assertEquals("int", ax.type.val);
	}
	@Test
	public void test4() {
		FullAssignmentExp ax = FullParser.parse01("let x int = 35");
		assertEquals("x", ax.var.val);
		assertEquals("int", ax.type.val);

		assertTrue(ax.value instanceof IntegerExp);
		assertEquals("35", ax.value.strValue());
	}
	@Test
	public void test4a() {
		FullAssignmentExp ax = FullParser.parse01("let x int = -35");
		assertEquals("x", ax.var.val);
		assertEquals("int", ax.type.val);

		assertTrue(ax.value instanceof IntegerExp);
		assertEquals("-35", ax.value.strValue());
	}
	@Test
	public void test4b() {
		FullAssignmentExp ax = FullParser.parse01("let x int = other");
		assertEquals("x", ax.var.val);
		assertEquals("int", ax.type.val);

		assertTrue(ax.value instanceof IdentExp);
		assertEquals("other", ax.value.strValue());
	}
    @Test
    public void test4c() {
        FullAssignmentExp ax = FullParser.parse01("let x int = 35;");
        assertEquals("x", ax.var.val);
        assertEquals("int", ax.type.val);

        assertTrue(ax.value instanceof IntegerExp);
        assertEquals("35", ax.value.strValue());
    }
    
    @Test
    public void test4h() {
        FullAssignmentExp ax = FullParser.parse01("let x number = 35.2");
        assertEquals("x", ax.var.val);
        assertEquals("number", ax.type.val);

        assertTrue(ax.value instanceof NumberExp);
        assertEquals("35.2", ax.value.strValue());
    }
    @Test
    public void test4i() {
        FullAssignmentExp ax = FullParser.parse01("let x long = 35");
        assertEquals("x", ax.var.val);
        assertEquals("long", ax.type.val);

        assertTrue(ax.value instanceof IntegerExp);
        assertEquals("35", ax.value.strValue());
    }
    @Test
    public void test4i2() {
        long big = Long.valueOf(Integer.MAX_VALUE);
        big += 1;
        
        FullAssignmentExp ax = FullParser.parse01(String.format("let x long = %d", big));
        assertEquals("x", ax.var.val);
        assertEquals("long", ax.type.val);

        assertTrue(ax.value instanceof LongExp);
        assertEquals("2147483648", ax.value.strValue());
    }
    

	@Test
	public void test5() {
		FullAssignmentExp ax = FullParser.parse01("let x string = 'some text'");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof StringExp);
		assertEquals("some text", ax.value.strValue());
	}
    @Test
    public void test5a() {
        //type name begins with keyword
        FullAssignmentExp ax = FullParser.parse01("let orx string = 'some text'");
        assertEquals("orx", ax.var.val);
        assertTrue(ax.value instanceof StringExp);
        assertEquals("some text", ax.value.strValue());
    }

	@Test
	public void test6() {
		FullAssignmentExp ax = FullParser.parse01("let x boolean = true");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof BooleanExp);
		assertEquals("true", ax.value.strValue());
	}

	@Test
	public void test7() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = RED");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof IdentExp);
		assertEquals("RED", ax.value.strValue());
	}

	@Test
	public void test8b() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = { }");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof StructAssignExp);
		StructAssignExp listexp = (StructAssignExp) ax.value;
		assertEquals(0, listexp.list.size());
	}
	@Test
	public void test9a() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = { 34, -35 }");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof StructAssignExp);
		StructAssignExp listexp = (StructAssignExp) ax.value;
		ListChecker.checkContentsExp(listexp.list, 34, -35);
	}
	@Test
	public void test9b() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = { y:34, z:-35 }");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof StructAssignExp);
		StructAssignExp listexp = (StructAssignExp) ax.value;
		assertEquals(2, listexp.list.size());
		StructMemberAssignExp exp = (StructMemberAssignExp) listexp.list.get(1);
		assertEquals("z", exp.strValue());
		assertEquals("-35", exp.value.strValue());
	}
	@Test
	public void test9c() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = { z, -35 }");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof StructAssignExp);
		StructAssignExp listexp = (StructAssignExp) ax.value;
		assertEquals(2, listexp.list.size());
		IntegerExp exp = (IntegerExp) listexp.list.get(1);
		assertEquals("-35", exp.strValue());
	}
	public void test9d() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = { y:z, z:-35 }");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof StructAssignExp);
		StructAssignExp listexp = (StructAssignExp) ax.value;
		assertEquals(2, listexp.list.size());
		StructMemberAssignExp exp = (StructMemberAssignExp) listexp.list.get(1);
		assertEquals("z", exp.strValue());
		assertEquals("-35", exp.value.strValue());
	}
    @Test
    public void test9e() {
        FullAssignmentExp ax = FullParser.parse01("let x Colour = { 23, null }");
        assertEquals("x", ax.var.val);
        assertTrue(ax.value instanceof StructAssignExp);
        StructAssignExp listexp = (StructAssignExp) ax.value;
        assertEquals(2, listexp.list.size());
        IdentExp exp = (IdentExp) listexp.list.get(1);
        assertEquals("null", exp.strValue());
    }
    @Test
    public void test9f() {
        FullAssignmentExp ax = FullParser.parse01("let x Colour = { y:z, z:null }");
        assertEquals("x", ax.var.val);
        assertTrue(ax.value instanceof StructAssignExp);
        StructAssignExp listexp = (StructAssignExp) ax.value;
        assertEquals(2, listexp.list.size());
        StructMemberAssignExp exp = (StructMemberAssignExp) listexp.list.get(1);
        assertEquals("z", exp.strValue());
        assertEquals("null", exp.value.strValue()); //is the string "null" not null
    }


	@Test
	public void test10() {
		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int < 0 end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(1, ax.ruleList.size());
		assertEquals("< 0", ax.ruleList.get(0).strValue());
	}
	@Test
	public void test10a() {
		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(0, ax.ruleList.size());
	}
	@Test
	public void test10b() {
		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int myrule(15) end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(1, ax.ruleList.size());
		CustomRule rule = (CustomRule) ax.ruleList.get(0);
		assertEquals("myrule", rule.ruleName);
		IntegerExp exp = (IntegerExp) rule.argL.get(0);
		assertEquals("15", exp.strValue());
	}
    @Test
    public void test10b1() {
        FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int !myrule(15) end");
        assertEquals("X", ax.var.val);
        assertEquals("int", ax.type.val);
        assertEquals(1, ax.ruleList.size());
        CustomRule rule = (CustomRule) ax.ruleList.get(0);
        assertEquals("myrule", rule.ruleName);
        IntegerExp exp = (IntegerExp) rule.argL.get(0);
        assertEquals("15", exp.strValue());
    }
	@Test
	public void test10c() {
		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int myrule('a') end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(1, ax.ruleList.size());
		CustomRule rule = (CustomRule) ax.ruleList.get(0);
		assertEquals("myrule", rule.ruleName);
		assertEquals("a", rule.argL.get(0).strValue());
	}
	@Test
	public void test10d() {
		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int myrule('a','b') end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(1, ax.ruleList.size());
		CustomRule rule = (CustomRule) ax.ruleList.get(0);
		assertEquals("myrule", rule.ruleName);
		assertEquals(2, rule.argL.size());
		assertEquals("a", rule.argL.get(0).strValue());
		assertEquals("b", rule.argL.get(1).strValue());
	}
	
//fix later!!
//	//for now the spaces are required 15 .. 20
//	@Test
//	public void test10e() {
//		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int myrule(15 ..20) end");
//		assertEquals("X", ax.var.val);
//		assertEquals("int", ax.type.val);
//		assertEquals(1, ax.ruleList.size());
//		CustomRule rule = (CustomRule) ax.ruleList.get(0);
//		assertEquals("myrule", rule.ruleName);
//		RangeExp exp = (RangeExp) rule.argL.get(0);
//		assertEquals("15..20", exp.strValue());
//	}
	
	@Test
	public void test11() {
		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int > 0 end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(1, ax.ruleList.size());
		assertEquals("> 0", ax.ruleList.get(0).strValue());
	}
	@Test
	public void test11a() {
		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int >= 0 end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(1, ax.ruleList.size());
		assertEquals(">= 0", ax.ruleList.get(0).strValue());
	}
	@Test
	public void test12() {
		FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int > 0  < 5 end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(2, ax.ruleList.size());
		assertEquals("> 0", ax.ruleList.get(0).strValue());
		assertEquals("< 5", ax.ruleList.get(1).strValue());
	}
    @Test
    public void test12a() {
        FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X string > 'a'  < 'z' end");
        assertEquals("X", ax.var.val);
        assertEquals("string", ax.type.val);
        assertEquals(2, ax.ruleList.size());
        assertEquals("> a", ax.ruleList.get(0).strValue());
        assertEquals("< z", ax.ruleList.get(1).strValue());
    }
	@Test
	public void test13() {
		FullTypeExp ax = (FullTypeExp)FullParser. parse02("type X int < 0 or > 5 end");
		assertEquals("X", ax.var.val);
		assertEquals("int", ax.type.val);
		assertEquals(1, ax.ruleList.size());
		ComparisonOrRuleExp exp = (ComparisonOrRuleExp) ax.ruleList.get(0);
		assertEquals("< 0", exp.exp1.strValue());
		assertEquals("> 5", exp.exp2.strValue());
	}

	@Test
	public void test20() {
		FullStructTypeExp ax = (FullStructTypeExp) FullParser.parse02("type X struct { } end");
		assertEquals("X", ax.var.val);
		assertEquals("struct", ax.type.val);
		assertEquals(0, ax.ruleList.size());
	}
	@Test
	public void test21() {
		FullStructTypeExp ax = (FullStructTypeExp) FullParser.parse02("type Position struct { x int optional unique y int } end");
		assertEquals("Position", ax.var.val);
		assertEquals("struct", ax.type.val);
		assertEquals(2, ax.members.list.size());
		StructMemberExp exp = (StructMemberExp) ax.members.list.get(0);
		assertEquals("x",exp.var.strValue());
		assertEquals("int",exp.type.strValue());
		assertEquals(true, exp.optional);
		assertEquals(true, exp.isUnique);
		exp = (StructMemberExp) ax.members.list.get(1);
		assertEquals("y",exp.var.strValue());
		assertEquals("int",exp.type.strValue());
		assertEquals(0, ax.ruleList.size());
		assertEquals(false, exp.optional);
        assertEquals(false, exp.isUnique);
	}
    @Test
    public void test22() {
        FullStructTypeExp ax = (FullStructTypeExp) FullParser.parse02("type Position struct { x int y list<int> } end");
        assertEquals("Position", ax.var.val);
        assertEquals("struct", ax.type.val);
        assertEquals(2, ax.members.list.size());
        StructMemberExp exp = (StructMemberExp) ax.members.list.get(0);
        assertEquals("x",exp.var.strValue());
        assertEquals("int",exp.type.strValue());
        exp = (StructMemberExp) ax.members.list.get(1);
        assertEquals("y",exp.var.strValue());
        assertEquals("list<int>",exp.type.strValue());
        assertEquals(0, ax.ruleList.size());
    }

	@Test
	public void test30() {
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
	public void test40() {
		FullEnumTypeExp ax = (FullEnumTypeExp) FullParser.parse02("type X enum { } end");
		assertEquals("X", ax.var.val);
		assertEquals("enum", ax.type.val);
		assertEquals(0, ax.ruleList.size());
	}
	@Test
	public void test41() {
		List<Exp> list = FullParser.fullParse("type X enum { RED BLUE } end");
		assertEquals(1, list.size());
		FullEnumTypeExp ax = (FullEnumTypeExp) list.get(0);
		assertEquals("X", ax.var.val);
		assertEquals("enum", ax.type.val);
		assertEquals(0, ax.ruleList.size());
	}
	@Test
	public void test42() {
		List<Exp> list = FullParser.fullParse("type Colour enum { RED BLUE } end let x Colour = BLUE");
		assertEquals(2, list.size());
		FullEnumTypeExp ax = (FullEnumTypeExp) list.get(0);
		assertEquals("Colour", ax.var.val);
		assertEquals("enum", ax.type.val);
		assertEquals(0, ax.ruleList.size());
		FullAssignmentExp bx = (FullAssignmentExp) list.get(1);
		assertEquals("Colour", bx.type.name());
		assertEquals("x", bx.var.name());
		IdentExp cx = (IdentExp) bx.value;
		assertEquals("BLUE", cx.name());
	}
	
	@Test
	public void test50() {
		FullListTypeExp ax = (FullListTypeExp) FullParser.parse02("type X list<int> end");
		assertEquals("X", ax.var.val);
		assertEquals("list", ax.type.val);
		assertEquals("list<int>", ax.elementType.name());
		assertEquals(0, ax.ruleList.size());
	}
	
	@Test
	public void test50a() {
		FullAssignmentExp ax = FullParser.parse01("let x list<int> = [ 34 ]");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof ListAssignExp);
		ListAssignExp listexp = (ListAssignExp) ax.value;
		ListChecker.checkContentsExp(listexp.list, 34);
	}
	@Test
	public void test51() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = [ 34 ]");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof ListAssignExp);
		ListAssignExp listexp = (ListAssignExp) ax.value;
		ListChecker.checkContentsExp(listexp.list, 34);
	}
	@Test
	public void test52() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = [ 34, 35 ]");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof ListAssignExp);
		ListAssignExp listexp = (ListAssignExp) ax.value;
		ListChecker.checkContentsExp(listexp.list, 34, 35);
	}
	@Test
	public void test53() {
		FullAssignmentExp ax = FullParser.parse01("let x Colour = [ ]");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof ListAssignExp);
		ListAssignExp listexp = (ListAssignExp) ax.value;
		assertEquals(0, listexp.list.size());
	}
	@Test
	public void test54() {
		FullAssignmentExp ax = FullParser.parse01("let x list<boolean> = [ ]");
		assertEquals("x", ax.var.val);
		assertTrue(ax.value instanceof ListAssignExp);
		ListAssignExp listexp = (ListAssignExp) ax.value;
		assertEquals(0, listexp.list.size());
	}
	

    @Test
    public void test60() {
        Date dt = new Date();
        long msecs = dt.getTime();
        System.out.println(String.format("x=%d", msecs));
        FullAssignmentExp ax = FullParser.parse01("let x date = 1481482266089");
        assertEquals("x", ax.var.val);
        assertEquals("date", ax.type.val);

        assertTrue(ax.value instanceof LongExp);
        assertEquals("1481482266089", ax.value.strValue());
    }
    @Test
    public void test61() {
        Date dt = new Date();
        long msecs = dt.getTime();
        System.out.println(String.format("x=%d", msecs));
        FullAssignmentExp ax = FullParser.parse01("let x date = 'sun jun 11'");
        assertEquals("x", ax.var.val);
        assertEquals("date", ax.type.val);

        assertTrue(ax.value instanceof StringExp);
        assertEquals("sun jun 11", ax.value.strValue());
    }
	
    @Test
    public void test70() {
        PackageExp ax = (PackageExp) FullParser.parse02("package x");
        assertEquals("x", ax.val);
        ax = (PackageExp) FullParser.parse02("package x.y");
        assertEquals("x.y", ax.val);
        ax = (PackageExp) FullParser.parse02("package x.y.Product");
        assertEquals("x.y.Product", ax.val);
    }
    @Test
    public void test71() {
        ImportExp ax = (ImportExp) FullParser.parse02("import x");
        assertEquals("x", ax.val);
        ax = (ImportExp) FullParser.parse02("import x.y");
        assertEquals("x.y", ax.val);
        ax = (ImportExp) FullParser.parse02("import x.y.Product");
        assertEquals("x.y.Product", ax.val);
    }
    @Test
    public void test80() {
        FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int z isa Product.id end");
        assertEquals("X", ax.var.val);
        assertEquals("int", ax.type.val);
        assertEquals(1, ax.ruleList.size());
        IsaRuleExp rule = (IsaRuleExp) ax.ruleList.get(0);
        assertEquals("z", rule.fieldName);
        assertEquals("Product.id", rule.val);
    }
    @Test
    public void test81() {
        FullTypeExp ax = (FullTypeExp) FullParser.parse02("type X int isa Product.id end");
        assertEquals("X", ax.var.val);
        assertEquals("int", ax.type.val);
        assertEquals(1, ax.ruleList.size());
        IsaRuleExp rule = (IsaRuleExp) ax.ruleList.get(0);
        assertEquals(null, rule.fieldName);
        assertEquals("Product.id", rule.val);
    }
    
    
    @Test
    public void test90() {
        FullAssignmentExp ax = FullParser.parse01("let x Colour = { Product via code 'abc', -35 }");
        assertEquals("x", ax.var.val);
        assertTrue(ax.value instanceof StructAssignExp);
        StructAssignExp listexp = (StructAssignExp) ax.value;
        assertEquals(2, listexp.list.size());
        IntegerExp exp = (IntegerExp) listexp.list.get(1);
        assertEquals("-35", exp.strValue());
        ViaExp via = (ViaExp) listexp.list.get(0);
        assertEquals("Product", via.typeExp.val);
        assertEquals("code", via.fieldExp.val);
        assertEquals("abc", via.valueExp.strValue());
    }
    @Test
    public void test91() {
        FullAssignmentExp ax = FullParser.parse01("let x Colour = { via code 'abc', -35 }");
        assertEquals("x", ax.var.val);
        assertTrue(ax.value instanceof StructAssignExp);
        StructAssignExp listexp = (StructAssignExp) ax.value;
        assertEquals(2, listexp.list.size());
        IntegerExp exp = (IntegerExp) listexp.list.get(1);
        assertEquals("-35", exp.strValue());
        ViaExp via = (ViaExp) listexp.list.get(0);
        assertEquals(null, via.typeExp);
        assertEquals("code", via.fieldExp.val);
        assertEquals("abc", via.valueExp.strValue());
    }
    @Test
    public void test92() {
        FullAssignmentExp ax = FullParser.parse01("let x Colour = Product via code 'abc'");
        assertEquals("x", ax.var.val);
        assertTrue(ax instanceof FullAssignmentExp);
        FullAssignmentExp listexp = (FullAssignmentExp) ax;
        ViaExp via = (ViaExp) listexp.value;
        assertEquals("Product", via.typeExp.val);
        assertEquals("code", via.fieldExp.val);
        assertEquals("abc", via.valueExp.strValue());
    }

}
