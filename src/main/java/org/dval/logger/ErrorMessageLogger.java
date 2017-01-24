package org.dval.logger;

import java.util.List;

import org.dval.ErrorMessage;

public class ErrorMessageLogger {

    public static void dump(List<ErrorMessage> errors) {
        if (errors.isEmpty()) {
            return;
        }
        Log.log(String.format("-----%d errors-----", errors.size()));
        for(ErrorMessage err : errors) {
            Log.log(String.format("%d: %s: %s", err.getLineNum(), err.getErrorType().name(), err.getMessage()));
        }
    }
    
}
