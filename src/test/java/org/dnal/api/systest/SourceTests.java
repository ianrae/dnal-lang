package org.dnal.api.systest;

import org.junit.Test;

/*
 */

public class SourceTests extends SysTestBase {

	@Test
	public void test() {
//		String dnal = "type Direction enum { NORTH,SOUTH,EAST,WEST } end  type ClassX struct {  direction1 Direction optional } end type ClassXDTO struct {  ddirection1 Direction optional } end inview ClassX <- ClassXDTOView { direction1 <- ddirection1 Direction } end"	;
		addSrc("type Direction enum { NORTH SOUTH EAST WEST } end "); // type ClassX struct {  direction1 Direction optional } end type ClassXDTO struct {  ddirection1 Direction optional } end inview ClassX <- ClassXDTOView { direction1 <- ddirection1 Direction } end"	;
        load(dnal, true);
	}
//    private void chkRule(String rule, String type, String value) {
//        String source = String.format("type Foo %s %s end let x Foo = %s", type, rule, value);
//        chkValue("x", source, 1, 1);
//    }
//    private void chkRuleFail(String rule, String type, String value) {
//        String source = String.format("type Foo %s %s end let x Foo = %s", type, rule, value);
//        load(source, false);
//    }

	//---
	private String dnal = "";
	
	private void addSrc(String s) {
		dnal += s;
	}
}