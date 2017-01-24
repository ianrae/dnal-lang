package com.github.ianrae.dnalparse.systest;


import org.dnal.api.impl.CompilerImpl;
import org.dnal.compiler.performance.PerfTimer;
import org.junit.Test;

public class PerfTests extends SysTestBase {
    
//    @Test
//    public void test() {
//        PerfTimer perf = new PerfTimer();
//        perf.startTimer("ABC");
//        for(int i = 0; i < 2; i++) {
//            perf.startTimer("def");
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            perf.endTimer("def");
//        }
//        perf.endTimer("ABC");
//        perf.dump();
//    }
    
    
    @Test
    public void test0() {
        chk("type Foo int end", 1, 0);
//        chk("type Foo int end", 1, 0);
        
        log("-----");
        CompilerImpl compiler = (CompilerImpl) this.aCompiler;
        compiler.getContext().perf.dump();
    }

}
