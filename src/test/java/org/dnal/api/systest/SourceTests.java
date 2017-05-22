package org.dnal.api.systest;

import org.junit.Test;

/*
 */

public class SourceTests extends SysTestBase {

	@Test
	public void test() {
//		String dnal = "type Direction enum { NORTH,SOUTH,EAST,WEST } end  type ClassX struct {  direction1 Direction optional } end type ClassXDTO struct {  ddirection1 Direction optional } end inview ClassX <- ClassXDTOView { direction1 <- ddirection1 Direction } end"	;
//		addSrc("type Direction enum { NORTH SOUTH EAST WEST } end "); // type ClassX struct {  direction1 Direction optional } end type ClassXDTO struct {  ddirection1 Direction optional } end inview ClassX <- ClassXDTOView { direction1 <- ddirection1 Direction } end"	;
        
//        addSrc("type ClassX struct {  strlist1 list<string> optional } end");
//        addSrc(" type ClassXDTO struct {  sstrlist1 list<string> optional } end ");
        
        add("type List100 list<Direction> end type Direction enum { NORTH SOUTH EAST WEST } end "); // type ClassX struct {  dirlist1 List100 optional } end type ClassXDTO struct {  ddirlist1 List100 optional } end inview ClassX <- ClassXDTOView { dirlist1 <- ddirlist1 List100 } end");
        load(dnal, true);
	}

	//---
	private String dnal = "";
	
	private void add(String s) {
		dnal += s;
	}
}