package org.dnal.compiler.dnalc;

import static org.junit.Assert.*;

import org.dnal.core.logger.Log;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogTests {

	
	@Test
	public void testLogback() {
		Logger logger = LoggerFactory.getLogger(DNALCTests.class);
		logger.info("Hello World");
		String s = "abc";
		logger.info("s is {}", s);
		s = null;
		logger.info("s is {}", s);
	}
	@Test
	public void testLog() {
		Log.log("Hello World");
//		String s = "abc";
//		Log.log("s is {}", s);
//		s = null;
//		Log.log("s is {}", s);
	}

}
