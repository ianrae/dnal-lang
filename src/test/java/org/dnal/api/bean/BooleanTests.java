package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.FieldSpec;
import org.junit.Before;
import org.junit.Test;


public class BooleanTests {

	@Test
	public void test() {
		addField("bb1", "b1");
		addField("bb2", "b2");
		chkCopy(dto, x, fields, true, true);
	}
	@Test
	public void test2() {
		addField("bb1", "b2");
		addField("bb2", "b1");
		chkCopy(dto, x, fields, true, true);
	}
	@Test
	public void testStr() {
		dto.sstr1 = "true";
		addField("sstr1", "b1");
		chkCopy(dto, x, fields);
		assertEquals(true, x.b1);
	}
	@Test
	public void testToStr() {
		addField("bb1", "str1");
		chkCopy(dto, x, fields);
		assertEquals("true", x.str1);
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
		dto.bb1 = true;
		dto.bb2 = true;
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields, boolean expected1, boolean expected2) {
		chkCopy(dto, x, fields);
		assertEquals(expected1, x.b1);
		assertEquals(expected2, x.b2.booleanValue());
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
