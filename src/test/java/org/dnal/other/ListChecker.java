package org.dnal.other;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.IntegerExp;


public class ListChecker<T> {
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
	public static void checkContents(List<? extends Comparable> list, Comparable... objects) {
		int i = 0;
		for(final Comparable val: objects) {
			Object obj = list.get(i);
			if (val.compareTo(obj) != 0) {
				assertEquals(val, obj);
			}
			i++;
		}
		assertEquals(objects.length, list.size());
	}
	
	public static void checkContentsInt(List<?> list, int... objects) {
		int i = 0;
		for(final int val: objects) {
			Object obj = list.get(i);
			Integer nval = (Integer) obj;
			if (val != nval.intValue()) {
				assertEquals(val, obj);
			}
			i++;
		}
		assertEquals(objects.length, list.size());
	}
	
	@SuppressWarnings("unchecked")
	public static void checkContentsExp(List<Exp> list, int... objects) {
		int i = 0;
		for(final int val: objects) {
			IntegerExp obj = (IntegerExp) list.get(i);
			if (val != obj.val) {
				assertEquals(val, obj);
			}
			i++;
		}
		assertEquals(objects.length, list.size());
	}
	
}