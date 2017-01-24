package com.github.ianrae.dnalparse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ListCheckerTest {
	
	@Test
	public void test() {
		List<String> list = new ArrayList<>();
		ListChecker<String> checker = new ListChecker<>(list);
		
		checker.isEmpty();
		checker.isSize(0);
		checker.contains();
		
		ListChecker.isEmpty(list);
		ListChecker.isSize(list, 0);
		ListChecker.checkContents(list);
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
		ListChecker.checkContents(list, "abc", "ABC");
	}

}
