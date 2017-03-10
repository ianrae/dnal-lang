package org.dnal.core.logger;

import java.util.List;

import org.dnal.core.NewErrorMessage;

public class ErrorMessageLogger {

    public static void dump(List<NewErrorMessage> errors) {
        if (errors.isEmpty()) {
            return;
        }
        Log.log(String.format("-----%d errors-----", errors.size()));
        for(NewErrorMessage err : errors) {
            Log.log(String.format("%d: %s: %s", err.getLineNum(), err.getErrorType().name(), err.getMessage()));
        }
    }
    
}
