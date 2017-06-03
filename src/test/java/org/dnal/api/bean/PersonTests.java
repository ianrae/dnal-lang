package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.FieldSpec;
import org.dnal.api.beancopier.BeanCopierImpl;
import org.junit.Before;
import org.junit.Test;


public class PersonTests {

	@Test
	public void test() throws Exception {
		addField("pperson1", "person1");
		Person person = new Person("bob", 22);
		chkCopy(dto, x, fields, person);
		assertEquals("a", x.person1.roles.get(0));
	}
	@Test
	public void test2() throws Exception {
		addField("ddirlist1", "dirlist1");
		List<Direction> list = Arrays.asList(Direction.NORTH, Direction.SOUTH);
		dto.ddirlist1 = list;
		chkCopy(dto, x, fields);
		assertEquals(list, x.dirlist1);
	}
	@Test
	public void test2a() throws Exception {
		addField("ddirlist1", "strlist1");
		List<Direction> list = Arrays.asList(Direction.NORTH, Direction.SOUTH);
		dto.ddirlist1 = list;
		chkCopy(dto, x, fields);
		List<String> strlist = Arrays.asList("NORTH", "SOUTH");
		assertEquals(strlist, x.strlist1);
	}
	
	@Test
	public void test3Fail() throws Exception {
		addField("ddirlist1", "nlist1");
		List<Direction> list = Arrays.asList(Direction.NORTH, Direction.SOUTH);
		dto.ddirlist1 = list;
		chkCopyFail(dto, x, fields);
	}
	
	@Test
	public void testListList() {
		addField("sstrlistlist1", "strlistlist1");
		
		List<String> list1 = Arrays.asList("A", "B");
		List<String> list2 = Arrays.asList("C", "D");
		List<List<String>> list = Arrays.asList(list1, list2);
		dto.sstrlistlist1 = list;
		chkCopy(dto, x, fields);
		assertEquals(list, x.strlistlist1);
	}
	
	@Test
	public void testStructList() {
		addField("ppersonList", "personList");
		List<Person> list = new ArrayList<>();
		list.add(new Person("bob", 22));
		list.add(new Person("sue", 23));
		
		dto.ppersonList = list;
		chkCopy(dto, x, fields);
		chkPerson(x.personList.get(0), "bob", 22);
		chkPerson(x.personList.get(1), "sue", 23);
		assertEquals(2, x.personList.size());
	}
	@Test
	public void testPersonGroupList() {
		addField("ppersonGroupList", "personGroupList");
		List<PersonGroup> list = new ArrayList<>();
		list.add(new PersonGroup("group1", new Person("bob", 22)));
		list.add(new PersonGroup("group2", new Person("sue", 23)));
		
		dto.ppersonGroupList = list;
		chkCopy(dto, x, fields);
		
		PersonGroup grp = x.personGroupList.get(0);
		assertEquals("group1", grp.getName());
		chkPerson(grp.getBoss(), "bob", 22);
		grp = x.personGroupList.get(1);
		assertEquals("group2", grp.getName());
		chkPerson(grp.getBoss(), "sue", 23);
		assertEquals(2, x.personGroupList.size());
	}


	//----------
//	private BeanCopier copier = new BeanCopierImpl();
	private BeanCopier copier = new BeanCopierImpl();
	private ClassXDTO dto = new ClassXDTO(0,0);
	private ClassX x = new ClassX();
	private List<FieldSpec> fields = new ArrayList<>();
	
	private void addField(String s1, String s2) {
		fields.add(new FieldSpec(s1, s2));
	}
	
	@Before
	public void init() {
		dto.pperson1 = new Person("bob", 22);
		dto.pperson1.roles = Arrays.asList("a");
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields, Person expected) {
		chkCopy(dto, x, fields);
		chkPerson(expected, x.person1.getName(), x.person1.getAge());
	}
	private void chkPerson(Person person, String name, int age) {
		assertEquals(name, person.getName());
		assertEquals(age, person.getAge());
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields) {
		boolean b = copier.copy(dto, x, fields);
		if (! b) {
			copier.dumpErrors();
		}
		assertEquals(true, b);
	}
	private void chkCopyFail(ClassXDTO dto, ClassX x, List<FieldSpec> fields) {
		boolean b = copier.copy(dto, x, fields);
		if (! b) {
			copier.dumpErrors();
		}
		assertEquals(false, b);
	}

	private void log(String s) {
		System.out.println(s);;
	}

	
}
