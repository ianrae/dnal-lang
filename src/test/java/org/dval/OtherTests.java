package org.dval;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class OtherTests {
	public static class Stack {
		private List<String> list = new ArrayList<>();

		//tos is end of list
		public void push(String s) {
			list.add(s);
		}

		public String pop() {
			String top = list.get(list.size() - 1);
			list.remove(list.size() - 1);
			return top;
		}

		public int size() {
			return list.size();
		}
		public boolean isEmpty() {
			return (size() == 0);
		}
	}

//	@Test
//	public void test() {
//		LOG.debug("debug msg");
//		LOG.info("info msg");
//		LOG.warn("warn msg");
//
//		String s1 = "abc";
//		String s2 = "abc";
//		assertEquals(true, s1.equals(s2));
//	}

	@Test
	public void testStack() {
		Stack stack = new Stack();
		assertEquals(true, stack.isEmpty());
		stack.push("a");
		assertEquals(false, stack.isEmpty());
		String s = stack.pop();
		assertEquals("a", s);

		stack.push("a");
		stack.push("b");
		s = stack.pop();
		assertEquals("b", s);
		s = stack.pop();
		assertEquals("a", s);
		assertEquals(true, stack.isEmpty());
	}

	@Test
	public void testDate() throws Exception {
		Date dt = new Date();
		log(dt.toString());

		String str_date="11-June-07";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy") ; 
		dt = sdf.parse(str_date);
		log(dt.toString());
	}


	private void log(String s) {
		System.out.println(s);
	}
}
