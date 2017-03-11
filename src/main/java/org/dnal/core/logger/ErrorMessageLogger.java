package org.dnal.core.logger;

import java.util.List;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.core.NewErrorMessage;

public class ErrorMessageLogger {

    public static void dump(List<NewErrorMessage> errors) {
        if (errors.isEmpty()) {
            return;
        }
        Log.log(String.format("-----%d errors-----", errors.size()));
        XErrorTracker errorTracker = new XErrorTracker();
        
        for(NewErrorMessage err : errors) {
        	String errmsg = errorTracker.errToString(err);
            Log.log(errmsg);
        }
    }
    
}
