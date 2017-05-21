package org.dnal.api.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dnal.api.DataSet;
import org.dnal.api.Transaction;
import org.dnal.api.bean.ReflectionBeanLoaderTest.ClassA;
import org.dnal.api.beancopier.BeanCopier;
import org.dnal.api.beancopier.BeanMethodInvoker;
import org.dnal.api.beancopier.BeanToDTypeBuilder;
import org.dnal.api.beancopier.FieldSpec;
import org.dnal.api.beancopier.ScalarConvertUtil;
import org.dnal.api.view.ViewLoader;
import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.performance.PerfTimer;
import org.dnal.core.DStructType;
import org.dnal.core.DValue;
import org.dnal.core.logger.Log;
import org.junit.Test;


public class IntegerTests {

	public static class ClassX {
		private int n1;
		private Integer n2;
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
	}
	public static class ClassXDTO {
		private int nn1;
		private Integer nn2;
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
	}


	@Test
	public void testBeanCopier() {
		BeanCopier copier = new BeanCopier();
		ClassXDTO dto = new ClassXDTO();
		dto.nn1 = 45;
		dto.nn2 = 46;
		ClassX x = new ClassX();

		List<FieldSpec> fields = new ArrayList<>();
		fields.add(new FieldSpec("nn1", "n1"));
		fields.add(new FieldSpec("nn2", "n2"));

		boolean b = copier.copy(dto, x, fields);
		if (! b) {
			copier.dumpErrors();
		}
		
		assertEquals(true, b);
		assertEquals(45, x.n1);
		assertEquals(46, x.n2.intValue());
	}


	private void log(String s) {
		System.out.println(s);;
	}

}
