package org.dnal.core;

import static org.junit.Assert.*;

import org.dnal.core.DValue;
import org.dnal.core.logger.Log;
import org.junit.Test;

public class LogTests  {

	@Test
	public void test() {
		Log.log("test log1 - will show");
		Log.debugLog("test log1 - won't show");
		Log.debugLogging = true;
		Log.debugLog("test log1 - will show");
		Log.debugLogging = false; //reset
	}

	@Test
	public void test2() {
		Log.useSLFLogging = true;
		Log.log("slf-test log1 - will show");
		Log.debugLog("slf-test log1 - won't show");
		Log.debugLogging = true;
		Log.debugLog("slf-test log1 - will show");
		Log.debugLogging = false; //reset
	}


}
