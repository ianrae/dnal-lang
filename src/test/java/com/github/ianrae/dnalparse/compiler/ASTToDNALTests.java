package com.github.ianrae.dnalparse.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.core.DStructHelper;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.repository.Repository;
import org.dnal.core.repository.World;
import org.junit.Test;

public class ASTToDNALTests extends BaseTest {
	
	@Test
	public void test1() {
		checkTypeGen("type Foo boolean end", "Foo", "BOOLEAN_SHAPE");
		checkTypeGen("type Foo int end", "Foo", "INTEGER_SHAPE");
		checkTypeGen("type Foo long end", "Foo", "LONG_SHAPE");
		checkTypeGen("type Foo string end", "Foo", "STRING_SHAPE");
	}
	
	@Test
	public void testStruct() {
		//unlike scalar types DVAL doesn't register 'struct' as a type, so baseType is null
		checkTypeGen("type Foo struct {  } end", "Foo", null);
		checkTypeGen("type Foo struct { x int y string } end", "Foo", null);
	}

	@Test
	public void testEnum() {
		//unlike scalar types DVAL doesn't register 'struct' as a type, so baseType is null
		checkTypeGen("type Foo enum {  } end", "Foo", null);
		checkTypeGen("type Foo enum { RED BLUE } end", "Foo", null);
	}
	@Test
	public void testValueEnum() {
		DValue dval = checkValueGen("type Foo enum { RED BLUE } end let x Foo = BLUE", "Foo");
		assertEquals("BLUE", dval.asString());
		assertEquals(true, dval.getType().isScalarShape());
		assertEquals(true, dval.getType().isShape(Shape.ENUM));
	}
	@Test
	public void testValueEnumFail() {
		failingValueGen("type Foo enum { RED BLUE } end let x Foo = YELLOW", "Foo");
	}
	
	
	@Test
	public void testList() {
		//unlike scalar types DVAL doesn't register 'list' as a type, so baseType is null
		checkTypeGen("type Foo list<int> end", "Foo", null);
	}
	@Test
	public void testValueList() {
		DValue dval = checkValueGen("type Foo list<int> end let x Foo = [ 23 ]", "Foo");
		List<DValue> list= dval.asList();
		assertEquals(1, list.size());
		assertEquals(23, list.get(0).asInt());
		assertEquals(false, dval.getType().isScalarShape());
		assertEquals(true, dval.getType().isShape(Shape.LIST));
	}
//	@Test
//	public void testValueEnumFail() {
//		failingValueGen("type Foo enum { RED BLUE } end let x Foo = YELLOW", "Foo");
//	}

	
	@Test
	public void testValueBool() {
		DValue dval = checkValueGen("type Foo boolean end let x Foo = true", "Foo");
		assertEquals(true, dval.asBoolean());
	}
	@Test
	public void testValueInt() {
		DValue dval = checkValueGen("type Foo int end let x Foo = 15", "Foo");
		assertEquals(15, dval.asInt());
	}
    @Test
    public void testValueLong() {
        DValue dval = checkValueGen("type Foo long end let x Foo = 15", "Foo");
        assertEquals(15, dval.asLong());
    }
	@Test
	public void testValueString() {
		DValue dval = checkValueGen("type Foo string end let x Foo = 'abc'", "Foo");
		assertEquals("abc", dval.asString());
	}
	@Test
	public void testValueStruct() {
		DValue dval = checkValueGen("type Foo struct { x int y long } end let x Foo = { 10, 11 }", "Foo");
		DStructHelper helper = new DStructHelper(dval);
		assertEquals(10, helper.getField("x").asInt());
		assertEquals(11, helper.getField("y").asLong());
	}
	
	@Test
	public void testValueStructErr() {
		failingValueGen("type Foo struct { x intxxx y int } end let x Foo = { 10, 11 }", "Foo");
	}
	
	private void checkTypeGen(String input, String typeName, String baseType) {
		log("doing: " + input);
		List<Exp> list = FullParser.fullParse(input);
		assertEquals(1, list.size());
		
		ASTToDNALGenerator dnalGenerator = createASTGenerator();
		boolean b = dnalGenerator.generate(list);
		dnalGenerator.dumpErrors();
		assertEquals(true, b);
		
		World world = this.getContext().world;
		world.dump();
		
		DTypeRegistry registry = getContext().registry;
		DType type = registry.getType(typeName);
		assertEquals(typeName, type.getName());
		
		if (baseType == null) {
			assertEquals(null, type.getBaseType());
		} else {
			assertEquals(baseType, type.getBaseType().getName());
		}
	}
	private DValue checkValueGen(String input, String typeName) {
		log("doing: " + input);
		List<Exp> list = FullParser.fullParse(input);
		assertEquals(2, list.size());
		
        ASTToDNALGenerator dnalGenerator = createASTGenerator();
		boolean b = dnalGenerator.generate(list);
		if (!b) {
			dnalGenerator.dumpErrors();
		}
		assertEquals(true, b);
		
		World world = getContext().world;
		world.dump();
		
        DTypeRegistry registry = getContext().registry;
		DType dtype = registry.getType(typeName);
		assertEquals(typeName, dtype.getName());
		
		Repository repo = world.getRepoFor(dtype);
		DValue dval = repo.getAll().get(0);
		assertNotNull(dval);
		return dval;
	}
	private void failingValueGen(String input, String typeName) {
		log("doing: " + input);
		List<Exp> list = FullParser.fullParse(input);
		assertEquals(2, list.size());
		
        ASTToDNALGenerator dnalGenerator = createASTGenerator();
		boolean b = dnalGenerator.generate(list);
		if (!b) {
			dnalGenerator.dumpErrors();
		}
		assertEquals(false, b);
	}
	

	private void log(String s) {
		System.out.println(s);
	}
}
