package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.FieldSpec;
import org.junit.Test;


public class IntegerTests {

	public static class ClassX {
		private int n1;
		private Integer n2;
		private short sh1;
		private Short sh2;
		private String str1;
		private BigDecimal bigd1;
		private long long1;
		private Long long2;
		
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
		public String getStr1() {
			return str1;
		}
		public void setStr1(String str1) {
			this.str1 = str1;
		}
		public BigDecimal getBigd1() {
			return bigd1;
		}
		public void setBigd1(BigDecimal bigd1) {
			this.bigd1 = bigd1;
		}
		public long getLong1() {
			return long1;
		}
		public void setLong1(long long1) {
			this.long1 = long1;
		}
		public Long getLong2() {
			return long2;
		}
		public void setLong2(Long long2) {
			this.long2 = long2;
		}
	}
	public static class ClassXDTO {
		private int nn1;
		private Integer nn2;
		private short sh1;
		private Short sh2;
		private String sstr1;
		private BigDecimal bbigd1;
		private long nlong1;
		private Long nlong2;

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
		public String getSstr1() {
			return sstr1;
		}
		public void setSstr1(String sstr1) {
			this.sstr1 = sstr1;
		}
		public BigDecimal getBbigd1() {
			return bbigd1;
		}
		public void setBbigd1(BigDecimal bbigd1) {
			this.bbigd1 = bbigd1;
		}
		public Short getSh2() {
			return sh2;
		}
		public void setSh2(Short sh2) {
			this.sh2 = sh2;
		}
		public long getNlong1() {
			return nlong1;
		}
		public void setNlong1(long nlong1) {
			this.nlong1 = nlong1;
		}
		public Long getNlong2() {
			return nlong2;
		}
		public void setNlong2(Long nlong2) {
			this.nlong2 = nlong2;
		}
	}


	@Test
	public void test() {
		addField("nn1", "n1");
		addField("nn2", "n2");
		chkCopy(dto, x, fields, 45, 46);
	}
	@Test
	public void test2() {
		addField("nn1", "n2");
		addField("nn2", "n1");
		chkCopy(dto, x, fields, 46, 45);
	}
	
	@Test
	public void test3() {
		addField("nn1", "sh1");
		addField("nn2", "sh2");
		chkCopy(dto, x, fields);
		assertEquals(45, x.sh1);
		assertEquals(46, x.sh2.shortValue());
		
		dto = new ClassXDTO(32768, -32769);
		x = new ClassX();
		chkCopyFail(dto, x, fields);
		assertEquals(0, x.sh1);
		assertEquals(null, x.sh2);
	}
	@Test
	public void test3bDuplicate() {
		dto.sh2 = Short.valueOf((short) 111);
		addField("sh2", "sh1");
		addField("sh2", "sh2");
		x = new ClassX();
		chkCopy(dto, x, fields);
		assertEquals(111, x.sh1);
		assertEquals(111, x.sh2.shortValue());
	}
	
	
	@Test
	public void test3IntToStr() {
		addField("nn1", "str1");
		chkCopy(dto, x, fields);
		assertEquals("45", x.str1);
	}
	@Test
	public void test3StrToInt() {
		dto = new ClassXDTO(45, 46);
		dto.sstr1 = "400";

		addField("sstr1", "n1");
		chkCopy(dto, x, fields);
		assertEquals(400, x.n1);
	}
	@Test
	public void test4() {
		addField("nn1", "bigd1");
		x = new ClassX();
		chkCopyFail(dto, x, fields);
		assertEquals(0, x.sh1);
		assertEquals(null, x.sh2);
	}
	
	@Test
	public void test5() {
		dto.nlong1 = 445;
		dto.nlong2 = Long.valueOf(446);
		addField("nlong1", "n1");
		addField("nlong2", "n2");
		chkCopy(dto, x, fields, 445, 446);

		x = new ClassX();
		fields = new ArrayList<>();
		addField("nn1", "long1");
		addField("nn2", "long2");
		chkCopy(dto, x, fields);
		assertEquals(45, x.long1);
		assertEquals(46, x.long2.intValue());
	}


	//----------
	private BeanCopier copier = new BeanCopier();
	private ClassXDTO dto = new ClassXDTO(45, 46);
	private ClassX x = new ClassX();
	private List<FieldSpec> fields = new ArrayList<>();
	
	private void addField(String s1, String s2) {
		fields.add(new FieldSpec(s1, s2));
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
