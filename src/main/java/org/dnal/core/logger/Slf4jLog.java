package org.dnal.core.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLog {
	private final Logger LOG = LoggerFactory.getLogger(Slf4jLog.class);
	
	public void log(String s) {
		LOG.info(s);
	}
	public void log(String fmt, Object... args) {
		LOG.info(fmt, args);
	}
}
