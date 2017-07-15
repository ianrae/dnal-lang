package org.dnal.api.systest;

import org.junit.Test;

/*
 */

public class SourceTests extends SysTestBase {

	@Test
	public void test() {
//        add("type Foo list<list<string>> end let x Foo = [ ['abc', 'def'], ['z'] ]");
        
//       add("type Person struct {  name string optional age int optional } end");
//		load(dnal, true);
		
		String path = "c:\\tmp\\file1.dnal";
		this.loadFile = true;
		this.load(path, true);
	}

	//---
	private String dnal = "";
	
	private void add(String s) {
		dnal += s;
	}
}