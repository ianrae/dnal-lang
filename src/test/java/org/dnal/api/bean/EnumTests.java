package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.FieldSpec;
import org.junit.Before;
import org.junit.Test;


public class EnumTests {

	@Test
	public void test() {
		addField("ddirection1", "direction1");
		chkCopy(dto, x, fields, Direction.WEST);
		
		Object x = Direction.values()[0];
		log (x.toString());
		getEnumValues(Direction.class);
	}
	
	List<String> getEnumValues(Class<?> clazz) {
	
		for(int i = 0; i < clazz.getEnumConstants().length; i++) {
			Object x = clazz.getEnumConstants()[i];
			log(x.toString());
		}
		return null;
	}

	@Test
	public void testToString() {
		addField("ddirection1", "str1");
		chkCopy(dto, x, fields);
		assertEquals("WEST", x.str1);
	}

	@Test
	public void testFromString() {
		dto.sstr1 = "SOUTH";
		addField("sstr1", "direction1");
		chkCopy(dto, x, fields, Direction.SOUTH);
	}

	@Test
	public void testFromStringFail() {
		dto.sstr1 = "boom";
		addField("sstr1", "direction1");
		chkCopyFail(dto, x, fields);
	}


	//----------
	private BeanCopier copier = new BeanCopier();
	private ClassXDTO dto = new ClassXDTO(0,0);
	private ClassX x = new ClassX();
	private List<FieldSpec> fields = new ArrayList<>();
	
	private void addField(String s1, String s2) {
		fields.add(new FieldSpec(s1, s2));
	}
	
	@Before
	public void init() {
		dto.ddirection1 = Direction.WEST;
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields, Direction expected) {
		chkCopy(dto, x, fields);
		assertEquals(expected, x.direction1);
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
