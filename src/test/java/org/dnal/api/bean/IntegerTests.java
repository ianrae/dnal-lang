package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.FieldSpec;
import org.junit.Before;
import org.junit.Test;


public class IntegerTests {

	public static class ClassX {
		private int n1;
		private Integer n2;
		private short sh1;
		private Short sh2;
		
		public int getN1() {
			return n1;
		}
		public void setN1(int n1) {
			this.n1 = n1;
		}
		public Integer getN2() {
			return n2;
		}
		public void setN2(Integer n2) {
			this.n2 = n2;
		}
		public short getSh1() {
			return sh1;
		}
		public void setSh1(short sh1) {
			this.sh1 = sh1;
		}
		public Short getSh2() {
			return sh2;
		}
		public void setSh2(Short sh2) {
			this.sh2 = sh2;
		}
	}
	public static class ClassXDTO {
		private int nn1;
		private Integer nn2;
		private short sh1;
		private Short sh2;
		
		public ClassXDTO(int nn1, Integer nn2) {
			super();
			this.nn1 = nn1;
			this.nn2 = nn2;
		}
		public int getNn1() {
			return nn1;
		}
		public void setNn1(int nn1) {
			this.nn1 = nn1;
		}
		public Integer getNn2() {
			return nn2;
		}
		public void setNn2(Integer nn2) {
			this.nn2 = nn2;
		}
		public short getSh1() {
			return sh1;
		}
		public void setSh1(short sh1) {
			this.sh1 = sh1;
		}
	}


	@Test
	public void test() {
		ClassXDTO dto = new ClassXDTO(45, 46);
		ClassX x = new ClassX();

		List<FieldSpec> fields = new ArrayList<>();
		fields.add(new FieldSpec("nn1", "n1"));
		fields.add(new FieldSpec("nn2", "n2"));
		chkCopy(dto, x, fields, 45, 46);
	}
	@Test
	public void test2() {
		ClassXDTO dto = new ClassXDTO(45, 46);
		ClassX x = new ClassX();

		List<FieldSpec> fields = new ArrayList<>();
		fields.add(new FieldSpec("nn1", "n2"));
		fields.add(new FieldSpec("nn2", "n1"));
		chkCopy(dto, x, fields, 46, 45);
	}
	
	@Test
	public void test3() {
		ClassXDTO dto = new ClassXDTO(45, 46);
		ClassX x = new ClassX();

		List<FieldSpec> fields = new ArrayList<>();
		fields.add(new FieldSpec("nn1", "sh1"));
		fields.add(new FieldSpec("nn2", "sh2"));
		chkCopy(dto, x, fields);
		assertEquals(45, x.sh1);
		assertEquals(46, x.sh2.shortValue());
	}

	//----------
	private BeanCopier copier = new BeanCopier();
	
	@Before
	public void init() {
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields, int expected1, int expected2) {
		chkCopy(dto, x, fields);
		assertEquals(expected1, x.n1);
		assertEquals(expected2, x.n2.intValue());
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields) {
		boolean b = copier.copy(dto, x, fields);
		if (! b) {
			copier.dumpErrors();
		}
		assertEquals(true, b);
	}

	private void log(String s) {
		System.out.println(s);;
	}

}
