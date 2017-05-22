package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.FieldSpec;
import org.dnal.compiler.dnalgenerate.DateFormatParser;
import org.junit.Before;
import org.junit.Test;


public class DateTests {

	@Test
	public void test() {
		addField("ddt1", "dt1");
		chkCopy(dto, x, fields, timestamp);
	}
	
	@Test
	public void test1() {
		Date sometime = getDate(2013, 3, 25);
		dto.ddt1 = sometime;
		addField("ddt1", "dt1");
		chkCopy(dto, x, fields, sometime);
	}
	
	@Test
	public void testStr() {
		Date sometime = getDate(2013, 3, 25);
        String s = DateFormatParser.format(sometime);
        log(s);
		
		dto.sstr1 = s;
		addField("sstr1", "dt1");
		chkCopy(dto, x, fields, sometime);
	}
	@Test
	public void testToStr() {
		Date sometime = getDate(2013, 3, 25);
        String s = DateFormatParser.format(sometime);
        log(s);
		
		dto.ddt1 = sometime;
		addField("ddt1", "str1");
		chkCopy(dto, x, fields);
		log(x.str1);
		assertEquals("Mon Mar 25 07:30:41 EDT 2013", x.str1);
	}
	

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
		dto.ddt1 = timestamp;
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields, Date expected) {
		chkCopy(dto, x, fields);
		assertEquals(expected, x.dt1);
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

	private Date getDate(int year, int mon, int day) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.MONTH, mon - 1);
	    cal.set(Calendar.DATE, day);
	    cal.set(Calendar.HOUR, 7);
	    cal.set(Calendar.MINUTE, 30);
	    cal.set(Calendar.SECOND, 41);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dt = cal.getTime();
	    return dt;
	}
	
}
