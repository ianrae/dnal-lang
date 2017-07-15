package org.dnal.compiler.performance;

public class PerfContinuingTimer {
	private PerfTimer perfTimer;
	private long duration;

	public void start() {
		perfTimer = new PerfTimer();
		perfTimer.startTimer("A");
	}
	public void end() {
		perfTimer.endTimer("A");
		duration += perfTimer.calcDuration("A");
	}
	
	public long getDuration() {
		return duration;
	}
}
