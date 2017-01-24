package com.github.ianrae.dnalparse.performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class PerfTimer {
    private Stack<PerfMark> stack = new Stack<>();
    private List<PerfMark> finishedL = new ArrayList<>();
    
    public void startTimer(String name) {
        PerfMark mark = new PerfMark();
        mark.name = name;
        mark.startTime = new Date();
        stack.push(mark);
    }
    public void endTimer(String name) {
        PerfMark mark = stack.pop();
        mark.name = name;
        mark.endTime = new Date();
        finishedL.add(mark);
    }
    //yyyy-MM-dd'T'HH:mm:ss.SSSZ;
    
    public void dump() {
        for(PerfMark mark: finishedL) {
            long duration = mark.endTime.getTime() - mark.startTime.getTime();
            String s = String.format("%10s - %d", mark.name, duration);
            System.out.println(s);
                    
        }
    }
}