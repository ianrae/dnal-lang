package org.dnal.core.logger;


public class Log {
	private static Slf4jLog slfLogger = null;
	public static boolean debugLogging = false;
	public static boolean useSLFLogging = false;
	
	public static void log(String s) {
		if (useSLFLogging) {
			createSLFIfNeeded();
			slfLogger.log(s);
		} else {
			System.out.println(s);
		}
	}
	private static void createSLFIfNeeded() {
		if (slfLogger == null) {
			slfLogger = new Slf4jLog();
		}
	}
	
	public static void debugLog(String s) {
		if (debugLogging) {
			log(s);
		}
	}
}
