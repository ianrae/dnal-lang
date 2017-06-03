package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.bean2.ZBeanCopierImpl;
import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.BeanCopierImpl;
import org.dnal.api.beancopier.FieldSpec;
import org.junit.Before;
import org.junit.Test;


public class NumberTests {

	@Test
	public void test() {
		addField("dd1", "d1");
		addField("dd2", "d2");
		chkCopy(dto, x, fields, 4.5, 4.6);
	}
	@Test
	public void test2() {
		addField("dd1", "d2");
		addField("dd2", "d1");
		chkCopy(dto, x, fields, 4.6, 4.5);
	}
	
	@Test
	public void test2a() {
		addField("dd1", "f1");
		addField("dd2", "f2");
		chkCopy(dto, x, fields);
		assertEquals(4.5, x.f1, DELTA);
		assertEquals(4.6, x.f2, DELTA);
	}
	@Test
	public void test2b() {
		addField("dd1", "f2");
		addField("dd2", "f1");
		chkCopy(dto, x, fields);
		assertEquals(4.5, x.f2, DELTA);
		assertEquals(4.6, x.f1, DELTA);
	}
	@Test
	public void test2c() {
		dto.ff1 = Float.valueOf((float) 9.1);
		dto.ff2 = Float.valueOf((float) 9.2);
		addField("ff1", "d1");
		addField("ff2", "d2");
		chkCopy(dto, x, fields);
		assertEquals(9.1, x.d1, DELTA);
		assertEquals(9.2, x.d2, DELTA);
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
	public void test3ToStr() {
		addField("dd1", "str1");
		chkCopy(dto, x, fields);
		assertEquals("4.5", x.str1);
	}
	@Test
	public void test3ToNumber() {
		dto.sstr1 = "400.25";

		addField("sstr1", "d1");
		chkCopy(dto, x, fields);
		assertEquals(400.25, x.d1, DELTA);
	}
	@Test
	public void test4() {
		addField("dd1", "bigd1");
		x = new ClassX();
		chkCopyFail(dto, x, fields);
		assertEquals(0, x.sh1);
		assertEquals(null, x.sh2);
	}
	
	@Test
	public void test5() {
		dto.nlong1 = 445;
		dto.nlong2 = Long.valueOf(446);
		addField("nlong1", "d1");
		addField("nlong2", "d2");
		chkCopy(dto, x, fields, 445, 446);
	}
	@Test
	public void test5a() {
		addField("dd1", "long1");
		addField("dd2", "long2");
		chkCopy(dto, x, fields);
		assertEquals(4, x.long1);
		assertEquals(4, x.long2.intValue());
	}


	//----------
//	private BeanCopier copier = new BeanCopierImpl();
	private BeanCopier copier = new ZBeanCopierImpl();
	private ClassXDTO dto = new ClassXDTO(45, 46);
	private ClassX x = new ClassX();
	private List<FieldSpec> fields = new ArrayList<>();
	private final static double DELTA = 0.0001;
	
	@Before
	public void init() {
		dto.dd1 = 4.5;
		dto.dd2 = Double.valueOf(4.6);
	}	
	private void addField(String s1, String s2) {
		fields.add(new FieldSpec(s1, s2));
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields, double expected1, double expected2) {
		chkCopy(dto, x, fields);
		assertEquals(expected1, x.d1, DELTA);
		assertEquals(expected2, x.d2.doubleValue(), DELTA);
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
