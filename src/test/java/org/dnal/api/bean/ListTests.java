package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.FieldSpec;
import org.junit.Before;
import org.junit.Test;


public class ListTests {

	@Test
	public void test() throws Exception {
		addField("sstrlist1", "strlist1");
		List<String> list = Arrays.asList("abc", "def");
		chkCopy(dto, x, fields, list);
	}
	
	//TODO: list of enums
	//TODO: list of list
	//TODO: list of structs
	


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
		dto.sstrlist1 = Arrays.asList("abc", "def");
	}
	private void chkCopy(ClassXDTO dto, ClassX x, List<FieldSpec> fields, List<String> expected) {
		chkCopy(dto, x, fields);
		assertEquals(expected, x.strlist1);
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
