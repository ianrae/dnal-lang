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


public class LongTests {

	@Test
	public void test() {
		addField("nlong1", "long1");
		addField("nlong2", "long2");
		chkCopy(dto, x, fields, 45, 46);
	}
	@Test
	public void test2() {
		addField("nlong1", "long2");
		addField("nlong2", "long1");
		chkCopy(dto, x, fields, 46, 45);
	}
	
	@Test
	public void test3() {
		addField("nlong1", "sh1");
		addField("nlong2", "sh2");
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
		addField("nlong1", "str1");
		chkCopy(dto, x, fields);
		assertEquals("45", x.str1);
	}
	@Test
	public void test3StrToLong() {
		dto = new ClassXDTO(45, 46);
		dto.sstr1 = "400";

		addField("sstr1", "long1");
		chkCopy(dto, x, fields);
		assertEquals(400, x.long1);
	}
	@Test
	public void test4() {
		addField("nlong1", "bigd1");
		x = new ClassX();
		chkCopyFail(dto, x, fields);
		assertEquals(0, x.sh1);
		assertEquals(null, x.sh2);
	}
	


	//----------
//	private BeanCopier copier = new BeanCopierImpl();
	private BeanCopier copier = new ZBeanCopierImpl();
	private ClassXDTO dto = new ClassXDTO(0, 0);
	private ClassX x = new ClassX();
	private List<FieldSpec> fields = new ArrayList<>();
	
	@Before
	public void init() {
		dto.nlong1 = 45L;
		dto.nlong2 = Long.valueOf(46);
	}
	private void addField(String s1, String s2) {
		fields.add(new FieldSpec(s1, s2));
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields, int expected1, int expected2) {
		chkCopy(dto, x, fields);
		assertEquals(expected1, x.long1);
		assertEquals(expected2, x.long2.intValue());
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
