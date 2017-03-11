package com.github.ianrae.dnalparse.systest;

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
		chkRulesFail("> 20 < 100", "> 20: > 20");
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
		String source = String.format("type Foo int %s end let x Foo = 14", rules);
		chkFail(source, 1, errMsg);
//		logErrors();
	}	
}
