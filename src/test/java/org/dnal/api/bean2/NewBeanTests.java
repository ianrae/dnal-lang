package org.dnal.api.bean2;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.dnal.api.bean.ClassX;
import org.dnal.api.bean.ClassXDTO;
import org.dnal.api.bean.Person;
import org.dnal.compiler.et.XErrorTracker;
import org.junit.Before;
import org.junit.Test;

public class NewBeanTests {
	@Test
	public void test() {
		List<String> fields = Arrays.asList("name", "age");
		String source = createForClass(Person.class, fields);
		assertEquals("", source);
		assertEquals("name:string;age:int;", zc.getOutputFieldsOutput());
		chkSuccess();
	}

	@Test
	public void testList() {
		List<String> fields = Arrays.asList("roles");
		String source = createForClass(Person.class, fields);
		assertEquals("LIST List1:list<String>;", source);
		assertEquals("roles:List1;", zc.getOutputFieldsOutput());
		chkSuccess();
	}

	@Test
	public void testListEnum() {
		List<String> fields = Arrays.asList("directions");
		String source = createForClass(Person.class, fields);
		assertEquals("ENUM Direction:Direction;LIST List1:list<Direction>;", source);
		assertEquals("directions:List1;", zc.getOutputFieldsOutput());
		chkSuccess();
	}

	@Test
	public void testAll() {
		List<String> fields = Arrays.asList("directions", "age", "name", "roles");
		String source = createForClass(Person.class, fields);
		log(source);
		chkSuccess();
	}
	
	@Test
	public void testX1() {
		List<String> fields = Arrays.asList("direction1", "dirlist1");
		String source = createForClass(ClassX.class, fields);
		chkSuccess();
		assertEquals("ENUM Direction:Direction;LIST List1:list<Direction>;", source);
		assertEquals("direction1:Direction;dirlist1:List1;", zc.getOutputFieldsOutput());
	}
	@Test
	public void testX1Inner() {
		List<String> fields = Arrays.asList("person1");
		String source = createForClass(ClassX.class, fields);
		chkSuccess();
		String s = "LIST List1:list<String>;ENUM Direction:Direction;LIST List2:list<Direction>;Person:Person;";
		assertEquals(s, source);
		assertEquals("person1:Person;", zc.getOutputFieldsOutput());
	}
	@Test
	public void testPersonList() {
		List<String> fields = Arrays.asList("personList");
		String source = createForClass(ClassX.class, fields);
		chkSuccess();
		log(source);
		String s = "LIST List1:list<String>;ENUM Direction:Direction;LIST List2:list<Direction>;Person:Person;LIST List3:list<Person>;";
		assertEquals(s, source);
		assertEquals("personList:List3;", zc.getOutputFieldsOutput());
	}
	
	@Test
	public void testTwoClasses() {
		List<String> fields = Arrays.asList("person1");
		String source = createForClass(ClassX.class, fields);
		chkSuccess();
		String s = "LIST List1:list<String>;ENUM Direction:Direction;LIST List2:list<Direction>;Person:Person;";
		assertEquals("person1:Person;", zc.getOutputFieldsOutput());
		assertEquals(s, source);
		
		fields = Arrays.asList("pperson1");
		source = createForClass(ClassXDTO.class, fields);
		chkSuccess();
		assertEquals(s, source);
		assertEquals("person1:Person;pperson1:Person;", zc.getOutputFieldsOutput());
	}
	
	@Test
	public void testTwoClasses2() {
		List<String> fields = Arrays.asList("person1");
		String source = createForClass(ClassX.class, fields);
		chkSuccess();
		String s = "LIST List1:list<String>;ENUM Direction:Direction;LIST List2:list<Direction>;Person:Person;";
		assertEquals("person1:Person;", zc.getOutputFieldsOutput());
		assertEquals(s, source);
		
		fields = Arrays.asList("nlist1");
		source = createForClass(ClassX.class, fields);
		chkSuccess();
		s = "LIST List1:list<String>;ENUM Direction:Direction;LIST List2:list<Direction>;Person:Person;LIST List3:list<Integer>;";
		assertEquals(s, source);
		assertEquals("person1:Person;nlist1:List3;", zc.getOutputFieldsOutput());
	}

	//--
	private ZCreator zc;
	private XErrorTracker et = new XErrorTracker();
	
	@Before 
	public void init() {
		zc = new ZCreator(et);
	}
	
	private void chkSuccess() {
		if (et.areErrors()) {
			et.dumpErrors();
		}
		assertEquals(false, et.areErrors());
	}

	private String createForClass(Class<?> clazz, List<String> fields) {
		boolean b = zc.createForClass(clazz, fields);
		chkSuccess();
		assertEquals(true, b);
		return zc.getGenOutput();
	}

	

	private void log(String s) {
		System.out.println(s);
	}
	
}
