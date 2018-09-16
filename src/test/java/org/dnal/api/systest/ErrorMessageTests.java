package org.dnal.api.systest;

import static org.junit.Assert.*;

import org.dnal.core.DValue;
import org.dnal.core.logger.Log;
import org.junit.Before;
import org.junit.Test;

public class ErrorMessageTests extends SysTestBase {

//	@Test
//	public void testOK() {
//		chkRules("< 100");
//	}
	@Test
	public void testValFail() {
		chkRulesFail("> 20, < 100", "compare-gt: > 20");
	}	
	
	@Test
	public void testSyntaxFail() {
//		String source =  "type User struct {\n firstName xstring\n, lastName string optional\n } firstName.len() <= 4 end";
		String source =  "type User struct {\n firstName string\n, lastName string optional\n }\b $$ firstName.len() <= 4 end";
		chkFail(source, 1, "isa or end expected, EOF encountered", 4);
	}
	
	@Test
	public void testSyntaxFail2() {
//		String source =  "type User struct {\n firstName xstring\n, lastName string optional\n } firstName.len() <= 4 end";
		String source =  "type \nUser $$ struct {\n firstName string\n, lastName string optional\n }\b firstName.len() <= 4 end";
		chkFail(source, 1, "isa or end expected, struct encountered", 2);
	}
	
	@Test
	public void testASTFail() {
		String source =  "type User struct {\n firstName xstring\n, lastName string optional\n } firstName.len() <= 4 end";
		chkFail(source, 1, "struct type 'User' - unknown xstring", 2);
	}

	//---
	@Before
	public void init() {
//		Log.debugLogging = true;
//		Log.debugLog("debugLog: ON");
//		Log.debugLogging = false; //reset
		
	}

	private void chkRules(String rules) {
		String source = String.format("type Foo int %s end let x Foo = 14", rules);
		DValue dval = chkValue("x", source, 1, 1);
		assertEquals(14, dval.asInt());
	}
	private void chkRulesFail(String rules, String errMsg) {
		String source = String.format("type Foo \nint %s \nend\n let x Foo = 14", rules);
		chkFail(source, 1, errMsg);
//		logErrors();
	}	
}
