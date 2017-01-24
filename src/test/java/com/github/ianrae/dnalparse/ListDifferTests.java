package com.github.ianrae.dnalparse;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;


public class ListDifferTests {
	
	public static class ListChecker<T> {
		private List<T> list;
		
		public ListChecker(List<T> list) {
			this.list = list;
		}
		
		public void isEmpty() {
			assertEquals(true, list.isEmpty());
		}
		public void isSize(int expected) {
			assertEquals(expected, list.size());
		}
		public void contains(final T... objects) {
			int i = 0;
			for(final T str: objects) {
				T obj = list.get(i);
				if (! str.equals(obj)) {
					assertEquals(str, obj);
				}
				i++;
			}
			isSize(objects.length);
		}
		

		//static versions
		public static void isEmpty(List<?> list) {
			ListChecker checker = new ListChecker<>(list);
			checker.isEmpty();
		}
		public static void isSize(List<?> list, int expected) {
			ListChecker checker = new ListChecker<>(list);
			checker.isSize(expected);
		}
		public static void contains(List<?> list, Object... objects) {
			int i = 0;
			for(final Object str: objects) {
				Object obj = list.get(i);
				if (! str.equals(obj)) {
					assertEquals(str, obj);
				}
				i++;
			}
			assertEquals(objects.length, list.size());
		}
		
		
	}

	@Test
	public void test() {
		List<String> list = new ArrayList<>();
		ListChecker<String> checker = new ListChecker<>(list);
		
		checker.isEmpty();
		checker.isSize(0);
		checker.contains();
		
		ListChecker.isEmpty(list);
		ListChecker.isSize(list, 0);
		ListChecker.contains(list);
	}
	
	public static class ListComp {

		public List<String> compare(List<String> list1, List<String> list2) {
			List<String> diffL = new ArrayList<>();
			
			int j = 0;
			int i = 0;
			while(i < list1.size()) {
				String s1 = list1.get(i);
				if (j < list2.size()) {
					String s2 = list2.get(j);
					if (s1.equals(s2)) {
					} else {
						int k = findInList(list1, i + 1, s2);
						if (k < 0) {
//							diffL.add("I" + s2);
							diffL.add(String.format("U%d:%s", j, s2));
						} else {
							diffL.add(String.format("D%d:%d", j, k-1));
							i = k; 
						}
					}
				} else {
					diffL.add(String.format("xD%d:%d", j, list1.size() - 1));
					i = list1.size();
				}
				
				i++;
				j++;
			}

			int remaining = list2.size() - j;
			if (remaining > 0) {
				List<String> tmp = new ArrayList<>();
				for(int k = j; k < list2.size(); k++) {
					tmp.add(list2.get(k));
				}
				String ss = flatten(tmp);
				diffL.add(String.format("I%d:%d[%s]", j, list2.size()-1, ss));
			}
			
			
			return diffL;
		}

		private String flatten(List<String> tmp) {
			StringTrail trail = new StringTrail();
			for(String s: tmp) {
				trail.add(s);
			}
			return trail.getTrail();
		}

		private int findInList(List<String> list1, int start, String s2) {
			for(int i = start; i < list1.size(); i++) {
				String s = list1.get(i);
				if (s.equals(s2)) {
					return i; 
				}
			}
			return -1;
		}
		
	}
	
	@Test
	public void test33() {
		String[] ar = new String[] { "a", "b", "c", "d", "e", "f" };
		List<String> list1 = Arrays.asList(ar);
		ar = new String[] { "a", "c", "d", "e", "f" };
		List<String> list2 = Arrays.asList(ar);
		ar = new String[] { "a",  "d", "e", "f" };
		List<String> list3 = Arrays.asList(ar);
		ar = new String[] { "a", "b", "c", "d", "e" };
		List<String> list4 = Arrays.asList(ar);

		chk(list1, list2);
		chk(list1, list3);
		chk(list1, list4);
//		assertEquals(22, diff.size());
	}
	@Test
	public void test34() {
		List<String> list1 = buildList("a", "b", "c", "d", "e", "f");
		List<String> list2 = buildList("a", "c", "d", "e", "f");
		List<String> list3 = buildList("a",  "d", "e", "f");
		List<String> list4 = buildList("a", "b", "c", "d", "e");
		List<String> list5 = buildList();

		chk(list1, list2);
		chk(list1, list3);
		chk(list1, list4);
		chk(list1, list5);
	}
	@Test
	public void test34a() {
		List<String> list1 = buildList("a", "b", "c", "d", "e", "f");
		List<String> list2 = buildList("a", "c", "d", "e", "f");
		List<String> list3 = buildList("a",  "d", "e", "f");
		List<String> list4 = buildList("f", "e", "d", "c", "b", "a");
		List<String> list5 = buildList();

		chk(list1, list5);
		chk(list5, list1);
		chk(list1, list4);
	}
	@Test
	public void test34c() {
		List<String> list1 = buildList("a");
		List<String> list2 = buildList("b");
		List<String> list5 = buildList();

		chk(list1, list1);
		chk(list1, list2);
		chk(list1, list5);
		chk(list5, list1);
		chk(list5, list5);
	}
	@Test
	public void test34d() {
		List<String> list1 = buildList("a", "b");
		List<String> list2 = buildList("a");
		List<String> list3 = buildList("b");
		List<String> list5 = buildList();

		chk(list1, list2);
		chk(list2, list1);
		chk(list1, list3);
		chk(list3, list1);
	}
	
	
	private List<String> buildList(String... params) {
		List<String> L = new ArrayList<>();
		for(String s: params) {
			L.add(s);
		}
		return L;
	}
	
	private void chk(List<String> list1, List<String> list2) {
		ListComp comp = new ListComp();
		List<String> diff = comp.compare(list1, list2);
		StringTrail trail = new StringTrail();
		for(String s: diff) {
			trail.add(s);
		}
		log("diff: " + trail.getTrail());
	}
	
	
	private void log(String s) {
		System.out.println(s);
	}
	
	
	@Test
	public void test111() {
		log("a");
		int n = 1000000;
		List<String> list = new ArrayList<>();
		for(int i = 0; i < n; i++) {
			list.add("aaaaa");
		}
		assertEquals(n, list.size());
		log("go..");
		Date dt = new Date();
		String target = "MMM555";
		int count = 0;
		for(int i = 0; i < n; i++) {
			String s = list.get(i);
			if (target.equals(s)) {
				count++;
			}
		}
		Date dtEnd = new Date();
		long diff = dtEnd.getTime() - dt.getTime();
		log(String.format("end %d - %d", count, diff));
	}
	@Test
	public void test2() {
		List<String> list = new ArrayList<>();
		list.add("abc");
		list.add("ABC");
		ListChecker<String> checker = new ListChecker<>(list);
		
		checker.isSize(2);
		checker.contains("abc", "ABC");
		
		ListChecker.isSize(list, 2);
		ListChecker.contains(list, "abc", "ABC");
	}

}
