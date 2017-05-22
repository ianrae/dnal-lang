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
	
//	@Test
//	public void test1() {
//		Date sometime = getDate(2013, 3, 25);
//		dto.ddt1 = sometime;
//		addField("ddt1", "dt1");
//		chkCopy(dto, x, fields, sometime);
//	}
//	
//	@Test
//	public void testStr() {
//		Date sometime = getDate(2013, 3, 25);
//        String s = DateFormatParser.format(sometime);
//        log(s);
//		
//		dto.sstr1 = s;
//		addField("sstr1", "dt1");
//		chkCopy(dto, x, fields, sometime);
//	}
//	@Test
//	public void testToStr() {
//		Date sometime = getDate(2013, 3, 25);
//        String s = DateFormatParser.format(sometime);
//        log(s);
//		
//		dto.ddt1 = sometime;
//		addField("ddt1", "str1");
//		chkCopy(dto, x, fields);
//		log(x.str1);
//		assertEquals("Mon Mar 25 07:30:41 EDT 2013", x.str1);
//	}
//	
//	@Test
//	public void testLong() {
//		addField("ddt1", "long1");
//		chkCopy(dto, x, fields);
//		
//		Date dt = new Date(x.long1);
//		assertEquals(dt, dto.ddt1);
//	}
//	@Test
//	public void testFromLong() {
//		dto.nlong1 = timestamp.getTime();
//		addField("nlong1", "dt1");
//		chkCopy(dto, x, fields);
//		assertEquals(timestamp, x.dt1);
//	}
//	

	//----------
	private BeanCopier copier = new BeanCopier();
	private ClassXDTO dto = new ClassXDTO(0,0);
	private ClassX x = new ClassX();
	private List<FieldSpec> fields = new ArrayList<>();
	private Date timestamp = new Date();
	
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
