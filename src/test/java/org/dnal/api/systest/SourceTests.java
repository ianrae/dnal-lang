package org.dnal.api.systest;

import org.junit.Test;

/*
 */

public class SourceTests extends SysTestBase {

	@Test
	public void test() {
        add("type Foo list<list<string>> end let x Foo = [ ['abc', 'def'], ['z'] ]");

		load(dnal, true);
	}

	//---
	private String dnal = "";
	
	private void add(String s) {
		dnal += s;
	}
}