package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.api.bean2.ZBeanCopierImpl;
import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.BeanCopierImpl;
import org.dnal.api.beancopier.FieldSpec;
import org.junit.Test;


public class IntegerTests {

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
//	private BeanCopier copier = new BeanCopierImpl();
	private BeanCopier copier = new ZBeanCopierImpl();
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
