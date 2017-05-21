package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.FieldSpec;
import org.junit.Before;
import org.junit.Test;


public class BooleanTests {

	public static class ClassX {
		boolean b1;
		Boolean b2;
		String str1;
		public boolean isB1() {
			return b1;
		}
		public void setB1(boolean b1) {
			this.b1 = b1;
		}
		public Boolean getB2() {
			return b2;
		}
		public void setB2(Boolean b2) {
			this.b2 = b2;
		}
		public String getStr1() {
			return str1;
		}
		public void setStr1(String str1) {
			this.str1 = str1;
		}
	}
	public static class ClassXDTO {
		boolean bb1;
		Boolean bb2;
		String sstr1;
		public ClassXDTO(boolean b, boolean c) {
			bb1 = b;
			bb2 = c;
		}
		public boolean isBb1() {
			return bb1;
		}
		public void setBb1(boolean bb1) {
			this.bb1 = bb1;
		}
		public Boolean getBb2() {
			return bb2;
		}
		public void setBb2(Boolean bb2) {
			this.bb2 = bb2;
		}
		public String getSstr1() {
			return sstr1;
		}
		public void setSstr1(String sstr1) {
			this.sstr1 = sstr1;
		}
	}

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
	private ClassXDTO dto = new ClassXDTO(true, true);
	private ClassX x = new ClassX();
	private List<FieldSpec> fields = new ArrayList<>();
	
	private void addField(String s1, String s2) {
		fields.add(new FieldSpec(s1, s2));
	}
	
	@Before
	public void init() {
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
